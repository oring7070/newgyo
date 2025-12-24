package kr.co.newgyo.join.service;

import kr.co.newgyo.user.dto.UserDto;
import kr.co.newgyo.user.entity.UserEntity;
import kr.co.newgyo.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();

        Boolean exists = userRepository.existsByUsername(username);

        if(exists){
            return;
        }

        UserEntity data = new UserEntity();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ADMIN");

        userRepository.save(data);
    }

}
