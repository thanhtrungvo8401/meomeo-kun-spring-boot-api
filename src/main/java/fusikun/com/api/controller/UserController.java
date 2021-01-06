package fusikun.com.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fusikun.com.api.dto.UserRequest;
import fusikun.com.api.dto.UserResponse;
import fusikun.com.api.exceptionHandlers.Ex_MethodArgumentNotValidException;
import fusikun.com.api.model.User;
import fusikun.com.api.service.UserService;
import fusikun.com.api.validator.UserDataValidate;
import javassist.NotFoundException;

@RestController
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	UserDataValidate userDataValidate;

	@GetMapping("/users")
	public ResponseEntity<Object> handleGetUsers() {
		List<User> users = userService.findAll();
		List<UserResponse> userResponses = users.stream().map(user -> new UserResponse(user))
				.collect(Collectors.toList());
		Long total = userService.count();
		return ResponseEntity.ok(new UsersManagement(userResponses, total));
	}

	@PostMapping("/users/create")
	public ResponseEntity<Object> handleCreateUser(@Valid @RequestBody UserRequest userRequest)
			throws Ex_MethodArgumentNotValidException, NotFoundException {
		// CUSTOM VALIDATE:
		userDataValidate.validate(userRequest);
		User user = userRequest.getUser();
		userService.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable Long id) throws NotFoundException {
		// Validate data exist or not:
		userDataValidate.validateExistById(id);
		User user = userService.findById(id);
		return ResponseEntity.ok(new UserResponse(user));
	}

	@PatchMapping("/users/{id}/update")
	public ResponseEntity<Object> handleUpdateUserById(@Valid @RequestBody UserRequest userRequest,
			@PathVariable Long id) throws NotFoundException, Ex_MethodArgumentNotValidException {
		// Validate data: (validate email, if password was posted => also validate)
		userRequest.setId(id);
		userDataValidate.validateExistById(id);
		userDataValidate.validate(userRequest);
		// Update user:
		User user = userService.findById(id);
		user.setEmail(userRequest.getEmail());
		if (userRequest.getPassword() != null) {
			user.setPassword(userRequest.getPassword());
		}
		userService.save(user);
		return ResponseEntity.ok(new UserResponse(user));
	}

	private class UsersManagement {
		List<UserResponse> list;
		Long total;

		@SuppressWarnings("unused")
		public UsersManagement() {
		}

		public UsersManagement(List<UserResponse> list, Long total) {
			this.list = list;
			this.total = total;
		}

		@SuppressWarnings("unused")
		public List<UserResponse> getList() {
			return list;
		}

		@SuppressWarnings("unused")
		public void setList(List<UserResponse> list) {
			this.list = list;
		}

		@SuppressWarnings("unused")
		public Long getTotal() {
			return total;
		}

		@SuppressWarnings("unused")
		public void setTotal(Long total) {
			this.total = total;
		}
	}
}
