package fusikun.com.api.validator;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import fusikun.com.api.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import fusikun.com.api.dtoREQ.RoleRequest;
import fusikun.com.api.exceptionHandlers.Ex_MethodNotAllowedException;
import fusikun.com.api.model.app.Role;
import fusikun.com.api.model.app.User;
import fusikun.com.api.exceptionHandlers.Ex_MethodArgumentNotValidException;
import fusikun.com.api.service.RoleService;
import fusikun.com.api.service.UserService;
import fusikun.com.api.utils.SpaceUtils;
import javassist.NotFoundException;

@Component
public class RoleDataValidate {
	@Autowired
	RoleValidator roleValidator;

	@Autowired
	RoleService roleService;

	@Autowired
	UserService userService;

	public final void validate(RoleRequest roleRequest) throws Ex_MethodArgumentNotValidException {
		// TRYM WHITE SPACE:
		roleRequest.setDescription(SpaceUtils.trymWhiteSpace(roleRequest.getDescription()));
		roleRequest.setRoleName(SpaceUtils.trymWhiteSpace(roleRequest.getRoleName()));
		// VALIDATION:
		BindException errors = new BindException(roleRequest, RoleRequest.class.getName());
		roleValidator.validate(roleRequest, errors);
		if (errors.hasErrors()) {
			throw new Ex_MethodArgumentNotValidException(errors.getBindingResult());
		}
	}

	public final void validateExistById(UUID id) throws NotFoundException {
		Role role = roleService.findRoleById(id);
		if (role == null) {
			throw new NotFoundException("Role with id=" + id + " is not existed!!");
		}
	}

	public final void validateNeverDeleteRole(UUID id) {
		Role role = roleService.findRoleById(id);
		if (Arrays.asList(Constant.ADMIN_ROLE, Constant.STUDENT_ROLE, Constant.ASSISTANT_ROLE).contains(role.getRoleName())) {
			throw new Ex_MethodNotAllowedException("These role can not be deleted!");
		}
	}

	public final void validateRoleIsUsedByUser(UUID id) {
		List<User> users = userService.findByRoleId(id);
		if (!users.isEmpty()) {
			throw new Ex_MethodNotAllowedException("Role is used!");
		}
	}
}
