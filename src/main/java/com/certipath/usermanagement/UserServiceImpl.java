package com.certipath.usermanagement.service;

import com.certipath.usermanagement.model.User;
import com.certipath.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setFullname(user.getFullname());
        existing.setEmail(user.getEmail());
        existing.setRole(user.getRole());
        existing.setActive(user.isActive());

        return userRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
