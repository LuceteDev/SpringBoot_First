import React, { useState, useEffect } from 'react';
import Header from '../components/Header.jsx';
import Footer from '../components/Footer.jsx';
import Section from '../components/Section.jsx';

// --- 이미지 리스트 데이터 (public 폴더 기준 경로로 수정) ---

const WinePage = () => {
  return (
    <>
      <Header />
      <Section></Section>
      <Footer></Footer>
    </>
  );
};

export default WinePage;
