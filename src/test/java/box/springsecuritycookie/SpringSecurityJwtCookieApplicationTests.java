package box.springsecuritycookie;

import box.springsecuritycookie.entities.Role;
import box.springsecuritycookie.entities.Ticket;
import box.springsecuritycookie.entities.User;
import box.springsecuritycookie.payload.requests.LoginRequest;
import box.springsecuritycookie.payload.requests.NewTicketCreationRequest;
import box.springsecuritycookie.payload.requests.RegistrationRequest;
import box.springsecuritycookie.services.AdminControlsServiceImpl;
import box.springsecuritycookie.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
class SpringSecurityJwtCookieApplicationTests {

	private final String userEmail = UUID.randomUUID().toString() + "@email.com";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	AdminControlsServiceImpl adminControlsService;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	private String getJwtCookie(String email, String password) throws Exception {
		LoginRequest loginRequest = new LoginRequest(email, password);
		String requestBody = objectMapper.writeValueAsString(loginRequest);

		return Objects.requireNonNull(mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
						).andExpect(MockMvcResultMatchers.status().isOk())
						.andReturn()
						.getResponse()
						.getCookie("__Host-auth-token"))
				.getValue();
	}

	private String obtainParameter(String email, String password, String requestBody, String path, String parameter) throws Exception {
		String jwtCookie = getJwtCookie(email, password);
		MvcResult returnResult = mockMvc.perform(MockMvcRequestBuilders.post(path)
						.contentType(MediaType.APPLICATION_JSON)
						.cookie(new Cookie("__Host-auth-token", jwtCookie))
						.content(requestBody)
				)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		ObjectNode responseObject = objectMapper.readValue(returnResult.getResponse().getContentAsString(), ObjectNode.class);

        return responseObject.get(parameter).asText();
	}

	private ResultActions deleteByIdAndPath(Long id, String path) throws Exception {
		String jwtCookie = getJwtCookie("admin@admin.com", "admin");
		String json = "{\"id\":"+id.intValue()+"}";

		return mockMvc.perform(MockMvcRequestBuilders.delete(path)
						.contentType(MediaType.APPLICATION_JSON)
						.cookie(new Cookie("__Host-auth-token", jwtCookie))
						.content(json)
				);

	}

	private void registerUser(String email, String password) throws Exception {
		NewTicketCreationRequest newTicketRequest = new NewTicketCreationRequest(Role.ADMIN, 2);
		String requestBody = objectMapper.writeValueAsString(newTicketRequest);

		String field = obtainParameter(
				"admin@admin.com",
				"admin",
				requestBody,
				"/api/v1/admin/controls/create_ticket",
				"secret"
		);

		RegistrationRequest registrationRequest = new RegistrationRequest(email, password, field);
		String registrationRequestBody = objectMapper.writeValueAsString(registrationRequest);

		ResultActions returnResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/registration")
						.contentType(MediaType.APPLICATION_JSON)
						.content(registrationRequestBody)
				)
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}


	@Test
	public void testLoginAdmin() throws Exception {
		getJwtCookie("admin@admin.com", "admin");

	}

	@Test
	public void testRestrictedAccessAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/greeting")
				.cookie(new Cookie("__Host-auth-token", getJwtCookie("admin@admin.com", "admin")))
		).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testTicketsObtainment() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/controls/tickets")
				.cookie(new Cookie("__Host-auth-token", getJwtCookie("admin@admin.com", "admin")))
		).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testUsersObtainment() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/controls/users")
				.cookie(new Cookie("__Host-auth-token", getJwtCookie("admin@admin.com", "admin")))
		).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testTicketCreation() throws Exception {
		NewTicketCreationRequest newTicketRequest = new NewTicketCreationRequest(Role.ADMIN, 2);
		String requestBody = objectMapper.writeValueAsString(newTicketRequest);

		String field = obtainParameter(
				"admin@admin.com",
				"admin",
				requestBody,
				"/api/v1/admin/controls/create_ticket",
				"secret"
		);

		assertNotNull(field);
	}

	@Test
	public void testDeleteTicket() throws Exception {
		NewTicketCreationRequest newTicketRequest = new NewTicketCreationRequest(Role.ADMIN, 2);
		String requestBody = objectMapper.writeValueAsString(newTicketRequest);

		String secretField = obtainParameter(
				"admin@admin.com",
				"admin",
				requestBody,
				"/api/v1/admin/controls/create_ticket",
				"secret"
		);

		Ticket ticket = adminControlsService.getTicket(secretField);

		deleteByIdAndPath(ticket.getId(), "/api/v1/admin/controls/ticket")
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void testDeleteAdminByAdmin() throws Exception {
		deleteByIdAndPath(1L, "/api/v1/admin/controls/user")
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testRegisterAndDeleteUser() throws Exception {
		registerUser(userEmail, "testtest123");

		User user = userDetailsService.loadUserByEmail(userEmail);


		deleteByIdAndPath(user.getId(), "/api/v1/admin/controls/user");
	}

	@Test
	public void testSignoutUser() throws Exception {
		String jwtCookieString = getJwtCookie("admin@admin.com", "admin");

		Cookie returnResult = (mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/signout")
						.contentType(MediaType.APPLICATION_JSON)
						.cookie(new Cookie("__Host-auth-token", jwtCookieString))
				)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn()
				.getResponse()
				.getCookie("__Host-auth-token"));

        assert(returnResult.getValue().isEmpty());
	}

}
