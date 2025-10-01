import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../css/login.css';
import { useAuth } from '../contexts/AuthContext';

const SignUpForm = ({ onClose, onFormOpen }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [username, setUsername] = useState('');
  const [address, setAddress] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');

  // 아직 프론트 영역, 여기에서 contexts/AuthContext 의 signup 호출
  const { signUp } = useAuth();
  const user_id = email.split('@')[0]; // 이메일 @ 앞부분

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }
    // camelCase 그대로 전송해야함
    const userData = {
      userId: user_id,
      email,
      password,
      username,
      address,
      phoneNumber,
    };

    const success = await signUp(userData);
    if (success) {
      setEmail('');
      setPassword('');
      setConfirmPassword('');
      setUsername('');
      setAddress('');
      setPhoneNumber('');

      // 회원가입 성공 시 자동으로 로그인 폼으로 이동
      onFormOpen('login');
    }
  };

  const handleModalContentClick = (e) => {
    e.stopPropagation();
  };

  const handlePhoneChange = (e) => {
    let value = e.target.value.replace(/\D/g, ''); // 숫자만 남기기
    if (value.length > 3 && value.length <= 7) {
      value = value.replace(/(\d{3})(\d+)/, '$1-$2');
    } else if (value.length > 7) {
      value = value.replace(/(\d{3})(\d{4})(\d+)/, '$1-$2-$3');
    }
    setPhoneNumber(value);
  };

  return (
    <section className="login-section" onClick={onClose}>
      <div className="section_center" onClick={handleModalContentClick}>
        <button className="close-button" onClick={onClose}>
          &times;
        </button>
        <h2>회원가입</h2>
        <hr />
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="email">이메일</label>
            <input
              type="email"
              id="email"
              required
              placeholder="이메일을 입력하세요"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="username">사용자 이름</label>
            <input
              type="text"
              id="username"
              required
              placeholder="사용자 이름을 입력하세요"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              required
              placeholder="비밀번호를 입력하세요"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="new-password"
            />
          </div>
          <div className="input-group">
            <label htmlFor="confirmPassword">비밀번호 확인</label>
            <input
              type="password"
              id="confirmPassword"
              required
              placeholder="비밀번호를 다시 입력하세요"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              autoComplete="new-password"
            />
          </div>
          <div className="input-group">
            <label htmlFor="address">주소</label>
            <input
              type="text"
              id="address"
              placeholder="주소를 입력하세요"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="phoneNumber">휴대폰 번호</label>
            <input
              type="tel"
              id="phoneNumber"
              placeholder="휴대폰 번호를 입력하세요"
              value={phoneNumber}
              onChange={handlePhoneChange}
            />
          </div>
          <button type="submit">회원가입</button>
        </form>
        <div className="additional-links">
          <a onClick={() => onFormOpen('login')}>로그인으로 돌아가기</a>
        </div>
      </div>
    </section>
  );
};

export default SignUpForm;
