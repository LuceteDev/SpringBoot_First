import React, { useState } from 'react';
import axios from 'axios';
import '../css/login.css';
import { useAuth } from '../contexts/AuthContext';

const FindIdForm = ({ onClose, onFormOpen }) => {
  const [username, setName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [result, setResult] = useState(null); // ✅ 결과 저장
  const { findId } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = await findId({ username, phoneNumber }); // DTO 형태로 전달
      setResult(data);
      alert(`이름 : ${data.userId}, 이메일 : ${data.email}`);
    } catch (error) {
      alert(error);
    }
  };

  // ✅ 전화번호 입력 시 하이픈 자동 추가
  const handlePhoneChange = (e) => {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length > 3 && value.length <= 7) {
      value = value.replace(/(\d{3})(\d+)/, '$1-$2');
    } else if (value.length > 7) {
      value = value.replace(/(\d{3})(\d{4})(\d+)/, '$1-$2-$3');
    }
    setPhoneNumber(value);
  };

  const handleModalContentClick = (e) => {
    e.stopPropagation();
  };

  return (
    <section className="login-section" onClick={onClose}>
      <div className="section_center" onClick={handleModalContentClick}>
        <button className="close-button" onClick={onClose}>
          &times;
        </button>
        <h2>아이디 | 이메일 찾기</h2>
        <hr />
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="username">이름</label>
            <input
              type="text"
              id="username"
              required
              placeholder="이름을 입력하세요"
              value={username}
              onChange={(e) => setName(e.target.value)}
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
          <button type="submit">아이디 찾기</button>
        </form>
        {/* ✅ 결과 표시 */}
        {result && (
          <div className="result-box">
            <p>아이디: {result.userId}</p>
            <p>이메일: {result.email}</p>
          </div>
        )}
        <div className="additional-links">
          <a onClick={() => onFormOpen('login')}>로그인으로 돌아가기</a>
          <br />
          <a onClick={() => onFormOpen('findPw')}>비밀번호 재설정</a>
        </div>
      </div>
    </section>
  );
};

export default FindIdForm;
