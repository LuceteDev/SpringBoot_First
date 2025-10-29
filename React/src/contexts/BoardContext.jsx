// src/contexts/BoardContext.jsx
import React, { createContext, useContext, useState, useCallback } from 'react';
// import axios from 'axios';
import api from '../api/axiosConfig';

import { useAuth } from './AuthContext'; // 로그인 상태 확인을 위해 AuthContext 사용

const BoardContext = createContext();

export const BoardProvider = ({ children }) => {
  const { isLoggedIn } = useAuth(); // AuthContext에서 로그인 상태 가져오기
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null); // 성공 메시지

  // 게시글 목록 불러오기
  // const fetchPosts = useCallback(async () => {
  //   setLoading(true);
  //   setError(null);
  //   try {
  //     // const response = await axios.get('http://localhost:8080/api/posts');
  //     const response = await axios.get('http://localhost:8080/api/posts', {
  //       withCredentials: true, // ✅ 쿠키 전송
  //     });
  //     if (Array.isArray(response.data)) {
  //       setPosts(response.data);
  //     } else {
  //       console.error('받아온 데이터가 배열이 아닙니다:', response.data);
  //       setPosts([]);
  //     }
  //   } catch (err) {
  //     console.error('게시글을 불러오는 데 실패했습니다.', err);
  //     setError('게시글을 불러오는 중 오류가 발생했습니다.');
  //     setPosts([]);
  //   } finally {
  //     setLoading(false);
  //   }
  // }, []);
  // 게시글 목록 불러오기
  const fetchPosts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get('/api/posts'); // ✅ 여기서 withCredentials 안써도 됨
      if (Array.isArray(response.data)) {
        setPosts(response.data);
      } else {
        setPosts([]);
      }
    } catch (err) {
      setError('게시글 불러오기 실패');
      setPosts([]);
    } finally {
      setLoading(false);
    }
  }, []);

  // 게시글 작성
  const createPost = useCallback(
    async (newPostData) => {
      setLoading(true);
      setError(null);
      setMessage(null);

      if (!isLoggedIn) {
        setError('게시글 작성 권한이 없습니다. 로그인해주세요.');
        setLoading(false);
        return false;
      }

      try {
        const response = await axios.post(
          'http://localhost:8080/api/posts',
          newPostData,
          {
            withCredentials: true,
          }
        );
        setMessage('게시글이 성공적으로 작성되었습니다.');
        fetchPosts(); // 작성 후 목록 새로고침
        return true;
      } catch (err) {
        console.error('게시글 작성에 실패했습니다.', err);
        if (err.response && err.response.status === 403) {
          setError('게시글 작성 권한이 없습니다. 다시 로그인해 주세요.');
        } else {
          setError(
            err.response?.data?.message || '게시글 작성 중 오류가 발생했습니다.'
          );
        }
        return false;
      } finally {
        setLoading(false);
      }
    },
    [isLoggedIn, fetchPosts]
  );

  // 게시글 수정 (추후 구현)
  const updatePost = useCallback(
    async (postId, updatedPostData) => {
      setLoading(true);
      setError(null);
      setMessage(null);
      if (!isLoggedIn) {
        setError('게시글 수정 권한이 없습니다. 로그인해주세요.');
        setLoading(false);
        return false;
      }
      try {
        await axios.put(
          `http://localhost:8080/api/posts/${postId}`,
          updatedPostData,
          {
            withCredentials: true,
          }
        );
        setMessage('게시글이 성공적으로 수정되었습니다.');
        fetchPosts();
        return true;
      } catch (err) {
        if (err.response && err.response.status === 403) {
          setError(
            '게시글 수정 권한이 없습니다. 본인의 게시글만 수정 가능합니다.'
          );
        } else {
          setError(
            err.response?.data?.message || '게시글 수정 중 오류가 발생했습니다.'
          );
        }
        return false;
      } finally {
        setLoading(false);
      }
    },
    [isLoggedIn, fetchPosts]
  );

  // 게시글 삭제 (추후 구현)
  const deletePost = useCallback(
    async (postId) => {
      setLoading(true);
      setError(null);
      setMessage(null);
      if (!isLoggedIn) {
        setError('게시글 삭제 권한이 없습니다. 로그인해주세요.');
        setLoading(false);
        return false;
      }
      try {
        await axios.delete(`http://localhost:8080/api/posts/${postId}`, {
          withCredentials: true,
        });
        setMessage('게시글이 성공적으로 삭제되었습니다.');
        fetchPosts();
        return true;
      } catch (err) {
        if (err.response && err.response.status === 403) {
          setError(
            '게시글 삭제 권한이 없습니다. 본인의 게시글만 삭제 가능합니다.'
          );
        } else {
          setError(
            err.response?.data?.message || '게시글 삭제 중 오류가 발생했습니다.'
          );
        }
        return false;
      } finally {
        setLoading(false);
      }
    },
    [isLoggedIn, fetchPosts]
  );

  return (
    <BoardContext.Provider
      value={{
        posts,
        loading,
        error,
        message,
        fetchPosts,
        createPost,
        updatePost,
        deletePost,
        setError,
        setMessage, // 외부에서 에러/메시지 직접 초기화할 경우를 대비
      }}
    >
      {children}
    </BoardContext.Provider>
  );
};

export const useBoard = () => useContext(BoardContext);
