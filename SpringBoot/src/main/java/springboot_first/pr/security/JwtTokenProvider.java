// package springboot_first.pr.security;

// import io.jsonwebtoken.*;
// import io.jsonwebtoken.security.Keys;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import springboot_first.pr.entity.User;

// import java.security.Key;
// import java.util.Date;

// // 💡 JWT 라이브러리를 사용하여 토큰 생성 및 유효성 검증을 수행하는 실제 구현체
// @Slf4j
// @Service 
// public class JwtTokenProvider implements TokenProvider {

//     private final Key key;
//     // 💡 [수정] Access Token과 Refresh Token의 만료 시간을 분리
//     private final long accessExpirationTime;
//     private final long refreshExpirationTime;


//     // 생성자를 통해 설정 파일의 값을 주입받고, 비밀 키와 만료 시간을 초기화합니다.
//     // 💡 [수정] 두 개의 만료 시간을 주입받습니다.
//     public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
//                             @Value("${jwt.access-expiration-time}") long accessExpirationTime,
//                             @Value("${jwt.refresh-expiration-time}") long refreshExpirationTime) {
//         // Base64 인코딩된 비밀 키 문자열을 바이트 배열로 디코딩하여 Key 객체로 만듭니다.
//         this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
//         this.accessExpirationTime = accessExpirationTime;
//         this.refreshExpirationTime = refreshExpirationTime;
//         log.info("JWT TokenProvider 초기화 완료. Access 만료 시간: {}ms, Refresh 만료 시간: {}ms", accessExpirationTime, refreshExpirationTime);
//     }

//     /**
//      * 기본 JWT 생성 로직 (재활용을 위해 분리)
//      */
//     private String generateToken(User user, long expirationTime) {
//         Date now = new Date();
//         Date expiryDate = new Date(now.getTime() + expirationTime);

//         return Jwts.builder()
//                 .setSubject(user.getUserId()) // 토큰의 제목(Subject)으로 userId를 사용
//                 .setIssuedAt(now) // 토큰 발급 시간
//                 .setExpiration(expiryDate) // 토큰 만료 시간
//                 // 💡 [개선] 토큰 타입(액세스/리프레시) 구분을 위한 클레임 추가
//                 .claim("type", expirationTime == accessExpirationTime ? "access" : "refresh") 
//                 .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘과 비밀 키로 서명
//                 .compact();
//     }


//     /**
//      * 사용자 정보를 기반으로 Access Token을 생성합니다. (TokenProvider 인터페이스 구현)
//      */
//     @Override
//     public String createAccessToken(User user) {
//         String token = generateToken(user, accessExpirationTime);
//         log.info("JWT Access Token 생성 완료: UserId: {}", user.getUserId());
//         return token;
//     }
    
//     /**
//      * 사용자 정보를 기반으로 Refresh Token을 생성합니다. (TokenProvider 인터페이스 구현)
//      */
//     @Override
//     public String createRefreshToken(User user) {
//         String token = generateToken(user, refreshExpirationTime);
//         log.info("JWT Refresh Token 생성 완료: UserId: {}", user.getUserId());
//         return token;
//     }
    
//     /**
//      * 토큰에서 userId를 추출하고 토큰의 유효성을 검증합니다.
//      */
//     @Override
//     public String getUserIdFromToken(String token) {
//         try {
//             // 토큰을 파싱하고 Claims(페이로드)를 추출합니다.
//             Claims claims = Jwts.parserBuilder()
//                     .setSigningKey(key) // 서버의 비밀 키로 서명 검증
//                     .build()
//                     .parseClaimsJws(token) // 토큰 유효성 검사 및 파싱
//                     .getBody();

//             // Subject에 저장했던 userId를 반환
//             return claims.getSubject();
            
//         } catch (SecurityException | MalformedJwtException e) {
//             log.error("잘못된 JWT 서명입니다.");
//             throw new IllegalArgumentException("Invalid JWT signature.");
//         } catch (ExpiredJwtException e) {
//             log.error("만료된 JWT 토큰입니다.");
//             throw new IllegalArgumentException("Expired JWT token.");
//         } catch (UnsupportedJwtException e) {
//             log.error("지원되지 않는 JWT 토큰입니다.");
//             throw new IllegalArgumentException("Unsupported JWT token.");
//         } catch (IllegalArgumentException e) {
//             log.error("JWT 토큰이 잘못되었습니다.");
//             throw new IllegalArgumentException("JWT claims string is empty or invalid.");
//         }
//     }
// }

package springboot_first.pr.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component; // @Service 대신 @Component로 사용 가능
import springboot_first.pr.entity.User;

import java.security.Key;
import java.util.Date;

// 💡 JWT 라이브러리를 사용하여 토큰 생성 및 유효성 검증을 수행하는 실제 구현체
@Slf4j
@Component // Component 또는 Service 사용 가능
public class JwtTokenProvider implements TokenProvider {

	private final Key key;
	private final long accessExpirationTime;
	private final long refreshExpirationTime;


	// 생성자를 통해 설정 파일의 값을 주입받아 초기화합니다.
	public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
							@Value("${jwt.access-expiration-time}") long accessExpirationTime,
							@Value("${jwt.refresh-expiration-time}") long refreshExpirationTime) {
		// Base64 인코딩된 비밀 키 문자열을 바이트 배열로 디코딩하여 Key 객체로 만듭니다.
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.accessExpirationTime = accessExpirationTime;
		this.refreshExpirationTime = refreshExpirationTime;
		log.info("JWT TokenProvider 초기화 완료. Access 만료 시간: {}ms, Refresh 만료 시간: {}ms", accessExpirationTime, refreshExpirationTime);
	}

	/**
	 * 기본 JWT 생성 로직 (Access/Refresh 공통)
	 */
	private String generateToken(User user, long expirationTime) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationTime);

		return Jwts.builder()
				.setSubject(user.getUserId()) // 토큰의 제목(Subject)으로 userId를 사용
				.setIssuedAt(now) // 토큰 발급 시간
				.setExpiration(expiryDate) // 토큰 만료 시간
				// 토큰 타입(액세스/리프레시) 구분을 위한 클레임 추가
				.claim("type", expirationTime == accessExpirationTime ? "access" : "refresh") 
				.signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘과 비밀 키로 서명
				.compact();
	}


	@Override
	public String createAccessToken(User user) {
		String token = generateToken(user, accessExpirationTime);
		log.info("JWT Access Token 생성 완료: UserId: {}", user.getUserId());
		return token;
	}
	
	@Override
	public String createRefreshToken(User user) {
		String token = generateToken(user, refreshExpirationTime);
		log.info("JWT Refresh Token 생성 완료: UserId: {}", user.getUserId());
		return token;
	}
	
	/**
	 * 토큰의 유효성을 검증합니다. (필터에서 주로 사용됨)
	 */
	@Override
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.warn("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.warn("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.warn("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.warn("JWT 토큰이 잘못되었습니다(null이거나 비어 있음).");
		}
		// 예외가 발생하면 false 반환
		return false;
	}


	/**
	 * 유효한 토큰에서 userId를 추출합니다.
	 */
	@Override
	public String getUserIdFromToken(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key) 
				.build()
				.parseClaimsJws(token) 
				.getBody()
				.getSubject();
		} catch (JwtException e) {
			log.error("토큰 파싱 실패: {}", e.getMessage());
			// 토큰 파싱 실패 시, Spring Security 필터 체인에서 적절히 처리할 수 있도록 RuntimeException으로 던집니다.
			throw new RuntimeException("JWT 토큰 정보 추출 실패: " + e.getMessage());
		}
	}
	
	/**
	 * Access Token의 만료 시간(밀리초)을 반환합니다.
	 */
	@Override
	public long getAccessExpirationMillis() {
		return accessExpirationTime; // 👈 구현 완료
	}
}