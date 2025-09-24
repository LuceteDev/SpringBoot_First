import React, { useState, createContext, useContext } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(() => {
    return sessionStorage.getItem('isLoggedIn') === 'true';
  });
  // ✅ 사용자 이름 상태 추가
  const [username, setUsername] = useState(() => {
    return sessionStorage.getItem('username') || null;
  });

  const login = async (userData) => {
    try {
      const response = await axios.post(
        'http://localhost:5000/api/auth/login',
        userData
      );
      if (response.status === 200) {
        setIsLoggedIn(true);
        // ✅ 응답에서 받은 사용자 이름을 세션 스토리지와 상태에 저장
        setUsername(response.data.username);
        sessionStorage.setItem('isLoggedIn', 'true');
        sessionStorage.setItem('username', response.data.username);
        alert('로그인 성공!');
        return true;
      }
    } catch (error) {
      if (error.response) {
        alert(error.response.data.message);
      } else {
        alert('로그인에 실패했습니다.');
      }
      return false;
    }
  };

  const logout = () => {
    setIsLoggedIn(false);
    // ✅ 로그아웃 시 사용자 이름 상태와 세션 스토리지에서 삭제
    setUsername(null);
    sessionStorage.removeItem('isLoggedIn');
    sessionStorage.removeItem('username');
    alert('로그아웃 되었습니다.');
  };

  const signUp = async (userData) => {
    try {
      const response = await axios.post(
        'http://localhost:5000/api/auth/register',
        userData
      );
      if (response.status === 201) {
        alert('회원가입이 성공적으로 완료되었습니다.');
        return true;
      }
    } catch (error) {
      if (error.response && error.response.status === 409) {
        alert('이미 존재하는 이메일입니다.');
      } else {
        alert('회원가입에 실패했습니다.');
      }
      return false;
    }
  };

  return (
    // ✅ AuthContext에 username도 포함하여 제공
    <AuthContext.Provider
      value={{ isLoggedIn, login, logout, signUp, username }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// 3. useAuth 훅이 AuthContext를 사용하도록 수정
export const useAuth = () => {
  return useContext(AuthContext);
};
