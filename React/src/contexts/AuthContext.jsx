import React, { createContext, useContext, useState } from 'react';
import axios from 'axios';

// Context 생성
const AuthContext = createContext();

// Provider
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const storedUser = sessionStorage.getItem('user');
    return storedUser ? JSON.parse(storedUser) : null;
  });
  const [isLoggedIn, setIsLoggedIn] = useState(
    () => !!sessionStorage.getItem('user')
  );

  // 로그인 함수
  // const login = async (loginData) => {
  //   console.log(loginData);
  //   try {
  //     const response = await axios.post(
  //       'http://localhost:8080/api/auth/login',
  //       {
  //         method: 'POST',
  //         headers: { 'Content-Type': 'application/json' },
  //         body: JSON.stringify(loginData), // loginData를 그대로 전송해야 함
  //       }
  //     );
  //     const userData = response.data;

  //     setUser(userData);
  //     setIsLoggedIn(true);
  //     sessionStorage.setItem('user', JSON.stringify(userData));

  //     return true;
  //   } catch (error) {
  //     console.error(error);
  //     return false;
  //   }
  // };
  // 로그인 함수
  const login = async (loginData) => {
    console.log('클라이언트에서 서버로 보낼 데이터:', loginData); // {emailOrIdOrPhone: 'j', password: 'j'}
    try {
      // 💡 수정! axios는 두 번째 인자로 데이터를 받습니다.
      const response = await axios.post(
        'http://localhost:8080/api/auth/login',
        loginData // loginData 객체 자체를 바로 전달
      );

      const userData = response.data;

      setUser(userData);
      setIsLoggedIn(true);
      sessionStorage.setItem('user', JSON.stringify(userData));

      return true;
    } catch (error) {
      console.error(error);
      return false;
    }
  };
  // 로그아웃
  const logout = () => {
    setUser(null);
    setIsLoggedIn(false);
    sessionStorage.removeItem('user');
  };

  // 회원가입 : 프론트 에서 호출하고 여기로 와서 실행 -> 바로 스프링부트 컨트롤러 호출
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
        alert('이미 존재하는 이메일입니다.');
      } else {
        alert('회원가입 실패!');
      }
      return false;
    }
  };

  return (
    <AuthContext.Provider value={{ user, isLoggedIn, login, logout, signUp }}>
      {children}
    </AuthContext.Provider>
  );
};

// 커스텀 훅
export const useAuth = () => useContext(AuthContext);
