package com.ino.myblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ino.myblog.config.auth.PrincipalDetail;
import com.ino.myblog.dto.CategoryDto;
import com.ino.myblog.dto.UserProfileDto;
import com.ino.myblog.model.*;
import com.ino.myblog.service.BoardService;
import com.ino.myblog.service.CategoryService;
import com.ino.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

//인증이 안된 사용자들이 출입 할 수 있는 경로 /auth
// 주소가 / 이면 index.jsp 허용
// static이하에 있는 /js/**, .css .i
@Controller
public class UserController {
    @Value("${ino.key}")
    private String inoKey;

    @Autowired
	private     UserService userService;
	@Autowired
	private BoardService boardService;
	@Autowired
	private CategoryService categoryService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/auth/joinForm")
    public String joinForm(){
        return "user/joinForm";
    }
	  @GetMapping("/auth/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {
        boolean isVerified = userService.verifyUser(token);
        if (isVerified) {
            return ResponseEntity.ok("인증 완료!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 실패.");
        }
    }
    @GetMapping("/user/checkEmailStatus")
    public ResponseEntity<?> checkEmailStatus(@AuthenticationPrincipal PrincipalDetail principalDetail) {
        String emailCheckStatus = principalDetail.getEmailCheck();
        return ResponseEntity.ok(emailCheckStatus);
    }
    @GetMapping("/auth/loginForm")
    public String loginForm(){
        return  "user/loginForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm(){return "user/updateForm";}

	@GetMapping("/user/{id}")
	public String userDetail(@PathVariable int id, Model model,
							 @AuthenticationPrincipal PrincipalDetail principalDetail,
							 @PageableDefault(size = 3,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
		UserProfileDto dto = userService.userProfile(id, principalDetail.getUser().getId());
		dto.setBoards(userService.userBoardList(id, pageable));
		model.addAttribute("userId", id);
		dto.setCategories(userService.userCategoryList(id));
		model.addAttribute("dto", dto);
		return "user/userDetail";
	}
	@GetMapping("/user/{userId}/category/{categoryId}")
	public String userCategoryDetail(@PathVariable int userId, @PathVariable int categoryId, Model model,
									 @AuthenticationPrincipal PrincipalDetail principalDetail,
									 @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		UserProfileDto dto = userService.userProfile(userId, principalDetail.getUser().getId());
		dto.setBoards(userService.userBoardListByCategory(userId, categoryId, pageable));
		dto.setCategories(userService.userCategoryList(userId));
		model.addAttribute("userId", userId);
		model.addAttribute("dto", dto);
		return "user/userDetail";
	}
/*	@GetMapping("/category/{parentId}/children")
	@ResponseBody
	public ResponseEntity<List<CategoryDto>> getChildCategories(@PathVariable int parentId) {
		try {
			List<Category> childCategories = categoryService.getChildCategories(parentId);
			List<CategoryDto> categoryDtos = childCategories.stream()
					.map(category -> new CategoryDto(category.getId(), category.getName()))
					.collect(Collectors.toList());
			return ResponseEntity.ok(categoryDtos);
		} catch (Exception e) {
			// 오류 처리
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}*/

	/*@GetMapping("/setCookie")
	public String setCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("authToken", "your_token_value_here");
		response.addCookie(cookie);
		return "Cookie set successfully";
	}
	@GetMapping("/readCookie")
	public String readCookie(@CookieValue(name = "authToken", required = false) String authToken) {
		if (authToken != null) {
			return "Auth Token: " + authToken;
		} else {
			return "Auth Token not found";
		}
	}*/
/*	@GetMapping("/image/upload")
	public String upload() {
		return "image/upload";
	}*/

    @GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) { // Data를 리턴해주는 컨트롤러 함수

		// POST방식으로 key=value 데이터를 요청 (카카오쪽으로)
		// Retrofit2
		// OkHttp
		// RestTemplate

		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody 오브젝트 생성
       MultiValueMap<String,String> params= new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","4e832c29b1a13a2c3bc9699fde40040f");
        params.add("redirect_uri","http://localhost:8000/auth/kakao/callback");
        params.add("code",code);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
				new HttpEntity<>(params, headers);

		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
		ResponseEntity<String> response = rt.exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST,
				kakaoTokenRequest,
				String.class
		);

		// Gson, Json Simple, ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		OauthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OauthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		System.out.println("카카오 엑세스 토큰 : "+oauthToken.getAccess_token());

		RestTemplate rt2 = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer "+oauthToken.getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 =
				new HttpEntity<>(headers2);

		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
		ResponseEntity<String> response2 = rt2.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST,
				kakaoProfileRequest2,
				String.class
		);
		System.out.println(response2.getBody());

		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}


		// UUID란 -> 중복되지 않는 어떤 특정 값을 만들어내는 알고리즘
		System.out.println("블로그서버 패스워드 : "+inoKey);

		User kakaoUser = User.builder()
                .username(kakaoProfile.getId()+"_of_KaKao")
				.password(inoKey)
                .email("of_kakao")
                .emailCheck(EmailCheck.YES)
				.oauth("kakao")
				.build();

		// 가입자 혹은 비가입자 체크 해서 처리
		User originUser = userService.findUser(kakaoUser.getUsername());

		if(originUser.getUsername() == null) {
			System.out.println("기존 회원이 아니기에 자동 회원가입을 진행합니다");
			userService.save(kakaoUser);
		}

		System.out.println("자동 로그인을 진행합니다.");
		// 로그인 처리
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), inoKey));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "redirect:/";
	}

}


