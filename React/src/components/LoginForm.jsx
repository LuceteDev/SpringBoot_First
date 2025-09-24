import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../css/login.css';

import { useAuth } from '../services/AuthContext';

const LoginForm = ({ onClose, onFormOpen }) => {
  const [text, settext] = useState('');
  const [password, setPassword] = useState('');
  // 1. loginError 상태를 추가합니다. 초기값은 false
  const [loginError, setLoginError] = useState(false);

  // useAuth 훅을 사용하여 login 함수를 가져옵니다.
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const success = await login({ text, password });
    if (success) {
      // 로그인 성공 시 모달 닫기
      setLoginError(false); // 오류 메시지 초기화
      onClose();
    } else {
      // 2. 로그인 실패 시 loginError 상태를 true로 설정
      setLoginError(true);
    }
  };

  const handleModalContentClick = (e) => {
    e.stopPropagation();
  };

  // 3. input 값이 변경될 때 loginError를 초기화하는 함수
  const handletextChange = (e) => {
    settext(e.target.value);
    setLoginError(false);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
    setLoginError(false);
  };

  return (
    <section className="login-section" onClick={onClose}>
      <div className="section_center" onClick={handleModalContentClick}>
        <button className="close-button" onClick={onClose}>
          &times;
        </button>

        <h2>Login</h2>
        <hr />
        <form onSubmit={handleSubmit}>
          {/* loginError 상태가 true일 때만 오류 메시지 표시 */}
          {loginError && (
            <div className="login-error-message">
              로그인 실패! 아이디 또는 비밀번호를 확인하세요.
            </div>
          )}
          <div className="input-group">
            <label htmlFor="email">아이디 | 이메일 | 휴대폰 번호</label>
            <input
              type="text"
              id="text"
              name="text"
              required
              placeholder="아이디 | 이메일 | 휴대폰 번호 중 하나를 입력하세요"
              value={text}
              // 4. onChange 핸들러를 수정된 함수로 연결
              onChange={handletextChange}
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              name="password"
              required
              placeholder="비밀번호를 입력하세요"
              value={password}
              // 5. onChange 핸들러를 수정된 함수로 연결
              onChange={handlePasswordChange}
            />
          </div>
          <div className="login-set">
            <input type="checkbox" name="id-save" value="id-save" /> 아이디 저장
          </div>
          <button className="login-button" type="submit">
            로그인
          </button>
        </form>
        <div className="additional-links">
          {/* onFormOpen 함수를 통해 다른 폼을 열도록 수정 */}
          <a onClick={() => onFormOpen('signup')}>회원가입</a> |
          <a onClick={() => onFormOpen('findId')}>아이디 찾기</a> |
          <a onClick={() => onFormOpen('findPw')}>비밀번호 재설정</a>
        </div>
        <p className="text_gp">간편 로그인</p>
        <div className="social-login">
          <div className="so-lo">
            <a href="/auth/kakao">
              <button className="ga-btn">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="30"
                  height="30"
                  fill="currentColor"
                  className="bi bi-chat-fill"
                  viewBox="0 0 16 16"
                >
                  <path d="M8 15c4.418 0 8-3.134 8-7s-3.582-7-8-7-8 3.134-8 7c0 1.76.743 3.37 1.97 4.6-.097 1.016-.417 2.13-.771 2.966-.079.186.074.394.273.362 2.256-.37 3.597-.938 4.18-1.234A9 9 0 0 0 8 15" />
                </svg>
              </button>
            </a>
          </div>
          <div className="so-lo">
            <a href="/auth/google">
              <button className="ga-btn2">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="30"
                  height="30"
                  fill="currentColor"
                  className="bi bi-google"
                  viewBox="0 0 16 16"
                >
                  <path d="M15.545 6.558a9.4 9.4 0 0 1 .139 1.626c0 2.434-.87 4.492-2.384 5.885h.002C11.978 15.292 10.158 16 8 16A8 8 0 1 1 8 0a7.7 7.7 0 0 1 5.352 2.082l-2.284 2.284A4.35 4.35 0 0 0 8 3.166c-2.087 0-3.86 1.408-4.492 3.304a4.8 4.8 0 0 0 0 3.063h.003c.635 1.893 2.405 3.301 4.492 3.301 1.078 0 2.004-.276 2.722-.764h-.003a3.7 3.7 0 0 0 1.599-2.431H8v-3.08z" />
                </svg>
              </button>
            </a>
          </div>
        </div>
      </div>
    </section>
  );
};

export default LoginForm;
