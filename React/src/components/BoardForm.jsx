// src/components/BoardForm.jsx (수정된 부분)
import React, { useState, useEffect } from 'react';
import '../css/login.css';
import { useAuth } from '../service/AuthService';
// ✅ useBoard 훅을 임포트합니다.
import { useBoard } from '../service/BoardService';

const BoardForm = ({ onClose, onFormOpen }) => {
  const { isLoggedIn, user } = useAuth(); // 로그인 상태와 사용자 정보
  // ✅ useBoard 훅을 사용하여 게시판 관련 상태와 함수를 가져옵니다.
  const {
    posts,
    loading,
    error,
    message,
    fetchPosts,
    createPost,
    setError,
    setMessage,
  } = useBoard(); // useBoard에서 관리하는 상태와 함수를 가져옴

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  // 이전의 message, error useState는 BoardContext로 옮겨갔으므로 여기서는 제거하거나 필요에 따라 사용

  useEffect(() => {
    fetchPosts(); // 컴포넌트 마운트 시 게시글 불러오기
    // BoardContext의 에러/메시지 상태도 초기화
    setError(null);
    setMessage(null);
  }, [fetchPosts, setError, setMessage]); // fetchPosts가 의존성에 추가

  // 게시글 작성 처리
  const handleSubmit = async (e) => {
    e.preventDefault();
    // BoardContext에서 에러/메시지 상태를 초기화
    setError(null);
    setMessage(null);

    if (!title || !content) {
      setError('제목과 내용을 모두 입력해주세요.'); // BoardContext의 setError 호출
      return;
    }

    // createPost 함수는 이미 로그인 상태를 내부에서 확인합니다.
    const success = await createPost({ title, content });
    if (success) {
      setTitle('');
      setContent('');
    }
  };
  // 모달 내용 클릭 시 이벤트 전파 중단 함수
  const handleModalContentClick = (e) => {
    e.stopPropagation(); // 모달 배경 클릭으로 인한 닫힘 방지
  };

  return (
    <section className="board-section" onClick={onClose}>
      <div className="section_center" onClick={handleModalContentClick}>
        <button className="close-button" onClick={onClose}>
          &times;
        </button>
        <h2>게시판</h2>
        {/* ✅ BoardContext의 error/message 상태를 표시 */}
        {error && <p className="error-message">{error}</p>}
        {message && <p className="success-message">{message}</p>}
        {loading && <p>게시글 불러오는 중...</p>} {/* 로딩 상태 표시 */}
        {/* 게시글 작성 폼 */}
        {isLoggedIn ? (
          <form onSubmit={handleSubmit} className="board-form">
            <p className="author-info">
              작성자: **{user?.username || '익명'}**
            </p>
            <input
              type="text"
              placeholder="제목"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="input-field"
              disabled={loading} // 로딩 중에는 입력 비활성화
            />
            <textarea
              placeholder="내용"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              className="textarea-field"
              disabled={loading} // 로딩 중에는 입력 비활성화
            ></textarea>
            <button type="submit" className="submit-button" disabled={loading}>
              {loading ? '작성 중...' : '게시글 작성'}
            </button>
          </form>
        ) : (
          <p className="login-prompt">
            게시글을 작성하려면
            <a
              href="#"
              onClick={(e) => {
                e.preventDefault();
                onClose();
                onFormOpen('login');
              }}
            >
              로그인
            </a>
            해주세요.
          </p>
        )}
        {/* 게시글 목록 */}
        <div className="posts-list">
          <h3>게시글 목록 ({posts.length}개)</h3>
          {posts.length === 0 && !loading ? ( // 로딩 중이 아닐 때만 '게시글 없음' 표시
            <p>게시글이 없습니다.</p>
          ) : (
            posts.map((post) => (
              <div key={post.id} className="post-item">
                <h4>{post.title}</h4>
                <p>{post.content}</p>
                <small>
                  작성자: {post.authorUsername || '알 수 없음'} | 작성일:{' '}
                  {new Date(post.created_at).toLocaleDateString()} | 조회수:{' '}
                  {post.views}
                </small>
              </div>
            ))
          )}
        </div>
      </div>
    </section>
  );
};

export default BoardForm;
