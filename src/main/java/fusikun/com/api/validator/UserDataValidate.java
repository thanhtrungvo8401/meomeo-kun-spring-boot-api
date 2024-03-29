package fusikun.com.api.validator;

import java.util.UUID;

import fusikun.com.api.exceptionHandlers.Ex_MethodNotAllowedException;
import fusikun.com.api.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import fusikun.com.api.dtoREQ.UserRequest;
import fusikun.com.api.exceptionHandlers.Ex_MethodArgumentNotValidException;
import fusikun.com.api.exceptionHandlers.Ex_NotAllowDeleteYourSelf;
import fusikun.com.api.model.app.JwtUserDetails;
import fusikun.com.api.model.app.User;
import fusikun.com.api.service.UserService;
import fusikun.com.api.utils.SpaceUtils;
import javassist.NotFoundException;

@Component
public class UserDataValidate {

	@Autowired
	UserValidator userValidator;

	@Autowired
	UserService userService;

	public final void validate(UserRequest userRequest) throws Ex_MethodArgumentNotValidException {
		// TRYM WHITE SPACE:
		userRequest.setEmail(SpaceUtils.trymWhiteSpace(userRequest.getEmail()));
		userRequest.setEmail(userRequest.getEmail().toLowerCase());
		// VALIDATION:
		BindException errors = new BindException(userRequest, UserRequest.class.getName());
		userValidator.validate(userRequest, errors);
		if (errors.hasErrors()) {
			throw new Ex_MethodArgumentNotValidException(errors.getBindingResult());
		}
	}

	public final void validateExistById(UUID id) throws NotFoundException {
		User user = userService.findById(id);
		if (user == null)
			throw new NotFoundException("User with id=" + id + "is not existed");
	}

	public final void  validateNeverDeleteUser(UUID id) {
		User user = userService.findById(id);
		if (user.getEmail().equals(Constant.EMAIL)) {
			throw new Ex_MethodNotAllowedException("This user can not be deleted!");
		}
	}

	public final void validateNotDeleteYourself(UUID id) {
		JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		User user = userService.findById(id);
		if (user.getEmail().equals(jwtUserDetails.getEmail())) {
			throw new Ex_NotAllowDeleteYourSelf(user.getEmail() + " is deleting yourself");
		}
	}

}
