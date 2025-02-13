package ru.eventlink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.dto.user.UserRequestDto;
import ru.eventlink.exception.IntegrityViolationException;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.mapper.UserMapper;
import ru.eventlink.model.User;
import ru.eventlink.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        log.info("The beginning of the process of finding all users");
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<User> users;

        if (CollectionUtils.isEmpty(ids)) {
            users = userRepository.findAll(pageRequest).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, pageRequest).getContent();
        }

        log.info("The user has been found");
        return userMapper.listUserToListUserDto(users);
    }

    @Override
    public List<UserDto> getAllUsersBySortRating(int from, int size) {
        log.info("The beginning of the process of finding all users by sort rating");
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "rating"));
        List<User> users = userRepository.findAll(pageRequest).getContent();
        log.info("The user by sort rating has been found");
        return userMapper.listUserToListUserDto(users);
    }

    @Override
    @Transactional
    public UserDto createUser(UserRequestDto requestDto) {
        log.info("The beginning of the process of creating a user");
        User user = userMapper.userRequestDtoToUser(requestDto);
        userRepository.findUserByEmail(user.getEmail()).ifPresent(u -> {
            throw new IntegrityViolationException("User with email " + u.getEmail() + " already exists");
        });
        user.setRating(0L);
        userRepository.save(user);
        log.info("The user has been created");
        return userMapper.userToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        log.info("The beginning of the process of deleting a user");
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id = " + userId + " not found"));
        userRepository.deleteById(userId);
        log.info("The user has been deleted");
    }

    @Override
    public boolean getUserExists(long userId) {
        log.info("The beginning of the process of checking a user");
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional
    public void updateRatingUser(long userId, int rating) {
        log.info("The beginning of the process of updating a user");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        user.setRating(user.getRating() + rating);
        log.info("The user has been updated");
    }
}