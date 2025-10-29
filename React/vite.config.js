import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    open: true, // ✅ 이 줄 추가하면 자동으로 브라우저 열림!
    port: 5173, // 포트 번호는 기본값 그대로 or 원하는 번호로 설정
  },
});
