package fusikun.com.api.filters;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import fusikun.com.api.exceptionHandlers.Ex_InvalidTokenException;
import fusikun.com.api.model.app.JwtUserDetails;
import fusikun.com.api.service.JwtUserDetailsService;
import fusikun.com.api.utils.Constant;
import fusikun.com.api.utils.ConstantMessages;
import fusikun.com.api.utils.IgnoreUrl;
import fusikun.com.api.utils.JwtTokenUtil;

@Component
public class AuthenticateFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		if (IgnoreUrl.isPublicUrl(request)) {
			chain.doFilter(request, response);
			return;
		}
		// check JWT
		final String authorizationHeader = request.getHeader(Constant.AUTH_AUTHORIZATION);
		String userId = null;
		String jwt = null;
		if (authorizationHeader != null && authorizationHeader.startsWith(Constant.AUTH_BEARER)) {
			jwt = authorizationHeader.substring(Constant.AUTH_BEARER_INDEX);
			userId = jwtTokenUtil.getUserInfoFromToken(jwt);
		} else {
			System.out.println("======= JWT does not start with 'bearer' OR NULL =======");
			throw new Ex_InvalidTokenException(ConstantMessages.INVALID_TOKEN);
		}

		if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			JwtUserDetails userDetails = jwtUserDetailsService.loadUserByUserId(UUID.fromString(userId));
			if (jwtTokenUtil.validateToken(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			} else {
				System.out.println("======= invalid TOKEN =======");
				throw new Ex_InvalidTokenException(ConstantMessages.INVALID_TOKEN);
			}
		} else {
			throw new Ex_InvalidTokenException(ConstantMessages.INVALID_TOKEN);
		}
		chain.doFilter(request, response);
	}
}
