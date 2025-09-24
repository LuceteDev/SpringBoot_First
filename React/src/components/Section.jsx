import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../css/Section.css';

const Section = () => {
  // 이미지 배열 만들기
  const images = [
    '/imges/wine_images/wine1.jpg',
    '/imges/wine_images/wine2.jpg',
    '/imges/wine_images/wine3.jpg',
    '/imges/wine_images/wine4.jpg',
    '/imges/wine_images/wine5.jpg',
    '/imges/wine_images/wine6.jpg',
    '/imges/wine_images/wine7.jpg',
    '/imges/wine_images/wine8.jpg',
    '/imges/wine_images/wine9.jpg',
    '/imges/wine_images/wine10.jpg',
  ];
  // 왼쪽 이미지 상태 관리 1, 2 번으로 기본값을 부여한 것!
  const [leftIdx, setLeftIdx] = useState(1);
  // 오른쪽 이미지 상태 관리
  const [rightIdx, setRightIdx] = useState(2);

  // 왼쪽 이미지 변경 함수
  const nextLeft = () => {
    const newIdx = (leftIdx + 1) % images.length;
    setLeftIdx(newIdx);
  };

  const prevLeft = () => {
    const newIdx = (leftIdx - 1 + images.length) % images.length;
    setLeftIdx(newIdx);
  };

  // 오른쪽 이미지 변경 함수
  const nextRight = () => {
    const newIdx = (rightIdx + 1) % images.length;
    setRightIdx(newIdx);
  };

  const prevRight = () => {
    const newIdx = (rightIdx - 1 + images.length) % images.length;
    setRightIdx(newIdx);
  };
  return (
    <section className="main-section">
      <div className="section">
        {/* left_section_area 시작*/}
        <div className="left_section_area">
          <div className="section-text">
            <h1>와인이란</h1>
            <p>
              와인의 어원은 라틴어의 "비넘(Vinum)"으로 "포도나무로 부터 만든
              술"이라는 의미입니다.
            </p>
            <p>
              세계 여러 나라에서 와인을 뜻하는 다양한 용어들을 사용하는데,
              이탈리아에서는 비노(Vino)
              <br />
              독일에서는 바인(Wein) 프랑스에서는 뱅(Vin) 미국과 영국에서는
              와인(Wine)으로 부르고 있습니다.
              <br />
              넓은 의미에서의 와인은 과실을 발효시켜 만든 알코올 함유 음료를
              말하지만 일반적으로 신선한 천연 과일인 순수한 포도만을 원료로
              발효시켜 만든 포도주를 의미합니다.
            </p>
          </div>
          {/* 이미지와 버튼을 같은 영역으로 만들기 */}
          <div className="img_button_are">
            <img
              // src="/imges/wine_images/wine1.jpg"
              src={images[leftIdx]}
              alt="Wine1 img"
              className="section_first_img"
            />
            <button onClick={prevLeft} className="arrow-button">
              &lt;
            </button>
            <button onClick={nextLeft} className="arrow-button">
              &gt;
            </button>
          </div>
        </div>
        {/* left_section_area 끝 */}
        {/* right_section_area 시작 */}
        <div className="right_section_area">
          {/* 이미지와 버튼을 같은 영역으로 만들기 */}
          <div className="img_button_are">
            <img
              // src="/imges/wine_images/wine1.jpg"
              src={images[rightIdx]}
              alt="Wine1 img"
              className="section_two_img"
            />
            <button onClick={prevRight} className="arrow-button">
              &lt;
            </button>
            <button onClick={nextRight} className="arrow-button">
              &gt;
            </button>
          </div>
          <div className="section-text">
            <h1>여러가지의 와인 종류</h1>
            <h2>신이 인간에게 준 최고의 선물 "와인"</h2>
            <p>
              와인은 총 여섯 종류로 구분할 수 있습니다
              <br />
              레드 와인(Red Wine) 화이트 와인(White Wine) 로제 와인(Rose Wine)
              <br />
              스파클링 와인(Sparkling Wine) 디저트 와인(Dessert Wine) <br />
              강화 와인(Fortified Wine)으로 구분할 수 있습니다.
              <br />
              <br />
              여러 종류가 있죠??
              <br />
              지금은 간단히 레드와 화이트, 그리고 로제와 스파클링/샴폐인에
              대해서만 설명해 볼게요.
              <br />
              <br />
            </p>
          </div>
          <p>
            현재 웹 페이지에서 볼 수 있는 와인은 제가 근무하는 근무지에서
            취급하는 와인입니다
          </p>
        </div>
        {/* right_section_area 끝 */}
      </div>
    </section>
  );
};

export default Section;
