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
  const login = async (emailOrIdOrPhone, password) => {
    try {
      const response = await axios.post(
        'http://localhost:8080/api/auth/login',
        {
          emailOrIdOrPhone,
          password,
        }
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
