import React from 'react';
import '../css/login.css';
import { useAuth } from '../service/AuthService';

// onFormOpen 프롭을 받도록 수정
const MyPageForm = ({ onClose, onFormOpen }) => {
  const { username } = useAuth(); // 현재 로그인된 사용자 정보

  const handleModalContentClick = (e) => {
    e.stopPropagation();
  };

  return (
    <section className="login-section" onClick={onClose}>
      <div className="section_center" onClick={handleModalContentClick}>
        <button className="close-button" onClick={onClose}>
          &times;
        </button>
        <h2>마이페이지</h2>
        <hr />
        <div className="input-group">
          <p>
            <strong>{username}</strong>님, <br />
            마이페이지에 오신 것을 환영합니다!
          </p>
          {/* 추가적인 사용자 정보 (예: 이름, 이메일)를 표시할 수 있습니다. */}
        </div>
        <div className="additional-links">
          {/* onFormOpen 함수를 사용하여 폼 전환 */}
          <a onClick={() => onFormOpen('findId')}>이메일 찾기 </a>|
          <a onClick={() => onFormOpen('findPw')}> 비밀번호 재설정</a>
        </div>
      </div>
    </section>
  );
};

export default MyPageForm;
