package com.sujit.personal_Finance_Manager;

import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PersonalFinanceManagerApplicationTests {

	@Autowired
	private UserRepository userRepository;

//	@Test
//	void contextLoads_and_userCanBeSaved() {
//		User user = User.builder()
//				.username("it@works.com")
//				.password("testpw")
//				.fullName("Integration")
//				.phoneNumber("1111111")
//				.build();
//		User saved = userRepository.save(user);
//		assertThat(saved.getId()).isNotNull();
//		assertThat(userRepository.findByUsername("it@works.com")).isPresent();

}
