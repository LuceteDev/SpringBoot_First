import React, { useState } from 'react';
import axios from 'axios';
import '../css/login.css';

const FindIdForm = ({ onClose, onFormOpen }) => {
  const [name, setName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [result, setResult] = useState(null); // ✅ 결과 저장

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        'http://localhost:5000/api/auth/find-id',
        {
          name,
          phone_number: phoneNumber,
        }
      );
      setResult(response.data); // 서버에서 받은 user_id, email 저장
    } catch (error) {
      if (error.response) {
        alert(error.response.data.message);
      } else {
        alert('서버와 연결할 수 없습니다.');
      }
    }
  };

  const handleModalContentClick = (e) => {
    e.stopPropagation();
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
            <label htmlFor="name">이름</label>
            <input
              type="text"
              id="name"
              required
              placeholder="이름을 입력하세요"
              value={name}
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
            <p>아이디: {result.user_id}</p>
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
