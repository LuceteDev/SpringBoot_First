import React from 'react';
import '../css/Footer.css';
const footer = () => {
  return (
    <footer>
      <div className="contact-info">
        <h2>Contact Information</h2>
        <p>WINE_SHOP : 광주광역시 남구 효덕로</p>
        <p>Phone : 010-1234-5678</p>
        <p>Email : Wine@land.ac.kr</p>
        <p>
          <a
            href="https://wallhere.com/ko/wallpaper/1287735"
            target="_blank"
            rel="noopener noreferrer"
            style={{ color: 'white' }}
          >
            사진 출처
          </a>
        </p>
        <p>
          <a
            href="http://www.wineland.co.kr/"
            target="_blank"
            rel="noopener noreferrer"
            style={{ color: 'white' }}
          >
            와인 정보 출처
          </a>
        </p>
      </div>
    </footer>
  );
};
export default footer;
