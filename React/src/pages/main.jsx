import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import '../css/index.css';
import App from './App.jsx';
import GlobalStyle from '../GlobalStyle';

import { BrowserRouter, Routes, Route } from 'react-router-dom';
import WinePage from './WinePage.jsx';
import { AuthProvider } from '../service/AuthService.jsx';
import { BoardProvider } from '../service/BoardService.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <GlobalStyle />
    <AuthProvider>
      <BoardProvider>
        <BrowserRouter>
          <Routes>
            {/* '/' 경로에서는 App 컴포넌트(메인 포트폴리오 허브)를 보여줍니다. */}
            <Route path="/" element={<App />} />
            {/* '/wine' 경로에서는 WinePage 컴포넌트를 보여줍니다. */}
            <Route path="/wine" element={<WinePage />} />
            {/* '/growth' 경로도 나중에 추가할 수 있습니다. */}
            {/* <Route path="/growth" element={<GrowthPage />} /> */}
          </Routes>
        </BrowserRouter>
      </BoardProvider>
    </AuthProvider>
  </StrictMode>
);
