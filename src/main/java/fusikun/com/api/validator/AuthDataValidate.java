package fusikun.com.api.validator;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fusikun.com.api.model.app.Auth;
import fusikun.com.api.service.AuthService;
import javassist.NotFoundException;


@Component	
public class AuthDataValidate {
	@Autowired
	AuthService authService;
	
	public final void validateExistById(UUID id) throws NotFoundException {
		Auth auth = authService.findById(id);
		if (auth == null) {
			throw new NotFoundException("Auth with id=" + id + " is not found");
		}
	}
}
	