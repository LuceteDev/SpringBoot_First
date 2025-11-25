import React, { createContext, useContext, useState } from 'react';
import axios from 'axios';

// Context 생성
const AuthService = createContext();

// Provider
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const storedUser = sessionStorage.getItem('user');
    return storedUser ? JSON.parse(storedUser) : null;
  });
  const [isLoggedIn, setIsLoggedIn] = useState(
    () => !!sessionStorage.getItem('user')
  );

  // ✅로그인 함수
  const login = async (loginData) => {
    console.log('클라이언트에서 서버로 보낼 데이터:', loginData); // {emailOrIdOrPhone: 'j', password: 'j'}
    try {
      // 💡 수정! axios는 두 번째 인자로 데이터를 받습니다.
      const response = await axios.post(
        'http://localhost:8080/api/auth/login',
        loginData // loginData 객체 자체를 바로 전달
      );

      const userData = response.data;

      // 전역 user 상태 변경
      setUser(userData);
      // 전역 로그인 상태 true로
      setIsLoggedIn(true);
      sessionStorage.setItem('user', JSON.stringify(userData));
      console.log('userData 확인' + userData);
      return userData;
    } catch (error) {
      console.error(error);
      return null;
    }
  };

  // ✅로그아웃
  const logout = () => {
    setUser(null);
    setIsLoggedIn(false);
    sessionStorage.removeItem('user');
  };

  // ✅회원가입 : 프론트 에서 호출하고 여기로 와서 실행 -> 바로 스프링부트 컨트롤러 호출
  const signUp = async (newUserData) => {
    console.log(newUserData);
    try {
      const response = await axios.post(
        'http://localhost:8080/api/auth/register',
        newUserData
      );
      if (response.status === 201) {
        alert('회원가입 성공!');
        return true;
      }
    } catch (error) {
      if (error.response && error.response.status === 409) {
        const serverMessage = error.response.data;
        if (serverMessage === 'EMAIL_ALREADY_EXISTS') {
          alert('이미 존재하는 이메일입니다.');
        } else if (serverMessage === 'PHONE_ALREADY_EXISTS') {
          alert('이미 존재하는 휴대폰 번호입니다.');
        } else {
          alert('중복된 정보가 있습니다.');
        }
      } else {
        alert('회원가입 실패!');
      }
      return false;
    }
  };

  // ✅아이디 / 이메일 찾기
  const findId = async (requestDTO) => {
    console.log('사용자 입력 점검', requestDTO);
    try {
      const response = await axios.post(
        'http://localhost:8080/api/auth/find-id',
        requestDTO
      );
      return response.data; // { user_id, email }
    } catch (error) {
      throw error.response?.data?.message || '아이디 찾기 실패';
    }
  };

  // ✅비밀번호 재설정
  const resetPassword = async (requestDTO) => {
    try {
      const response = await axios.post(
        'http://localhost:8080/api/auth/reset-pw',
        requestDTO
      );
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || '비밀번호 재설정 실패';
    }
  };

  return (
    <AuthService.Provider
      value={{ user, isLoggedIn, login, logout, signUp, findId, resetPassword }}
    >
      {children}
    </AuthService.Provider>
  );
};

// 커스텀 훅
export const useAuth = () => useContext(AuthService);
