import React from 'react';

// --- Base64 데이터 변수들을 여기에 모두 정의 ---
const instagramLogoBase64 =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAABOFBMVEVHcExxF/13F/1zFf2AFv2MCu+iBOmyAuLEAd7SANraBMzwHpedI+XeANfmANHmAr+PF/6fD/vxAL7qArV7GP21FvnRVurnZ+P2Z9roQeDzGtXzB8v7AqvWiPn+7fn////7ftv/A7z0BKH3v/H+3fT+ruj+AJr/9/r/RcD/bMDKFff/Bo3iG+r8GrX/AIH+H5r/yeP+BHj/t9n/V5r+Am7+H37+BGT/ssX/Imz/RXv/ucz+e4z+JFT/XI3/MGL+LUf/u7f/yc7/Alr+OUf+MSz+QzT+R0T+iHD+SRj+VzH+WQX+AkL+c2L/0sT+aCX+cBL/7+j+aQL+eRv/oUX/fwL+iBD/wXz+jgD/3rr/1rn+dgz+nAP9FpT+lw7+qgH+tQD+vQD/w1f9kwf/xAD9PFn9ogj/ywD9uAfgLZLBAAAAaHRSTlMAW9H+////////xEsC///N////zFz/////////////////uP////7///////////7////////////////////////////////////////////////////////F/7VQ///+/87/vsj/uqL0GQwAAAHcSURBVHgBRMlFehwxEEDhV1VS04yZ7U04m+AmcBjfLLscJ4xLHyA0YGqSlJ7P9MT6BRC52EEQuDjlFITx2eWvXJsDQWYIKgJj4VQW5gBJAg75K5vTgZDTFUEu/kUEzqGaiC6DMBbhpqQyoyKTNYD1Hp+Ui6J0ZH1HhXNA0RfzyRIZl5WprroK3BZwwvx0aUGdB87HCIN5VBUVP9i468ZLxWLv+pg6vJmDaSiSW2eyIRNg84/HxfVJmTD3DxFWGmWjrosRpxMNGr3iIqYbG7IGK5lKvZE3jVuTynsDzHRoD3xpxaTQvIKumGBWgCouM9bPKEisJg0VTV4X0aBqQEuXQ2kGlmVmZCtgCoxBJfeAz0b8NQbWI7wOAKCZMgbn9E6tItG1HFTKRQ4jgSYpiiPu6BFJW1fW9J7oDFWWgpf0MH09YnB6yIEYnIJhLhqkpw3aC4OuUJ4QlDzX55/Um5m4Ue6dmXr59LwkBM0xy/ieVNV7zSSTZMU3fs8UNBomr+v3jQx5X1XmB7tdgnNu1JqDl+0nbnp+HpLp+WM5bB0mIMJFc/ouSgjd+U99O1qYONOhFEMonPcZCZ6eGp9eRcSwhWkSRCIxKnfeIMBh1WRcFOhTaENo/096OTA7AACDzKODDThakgAAAABJRU5ErkJggg==';

const notionLogoBase64 =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACsElEQVRYhb1X4XGrMAz+fPf+15kgGoERnAnCmyB0gsAEoRNAJ4BOkGxANkg3sN8EkAn0fgRT2xCatLl8dz7HSEifJZlYAjNgZglAAqB+dseyV5uSdQAOAN6EEGbOh+gdEYAtgCgweBeMufiSUkLK4fVcCPF2lUDv/OQ67LrOG8YYnM/n0bNQz0We59jtdnb5CeDvVDQEMzcA1Pv7O8qyHHYxB7tDKSWIyFvbSNR1DSJC0zQgIuCSlkwIUYcEGAAWiwW6rkMURYjjGC8vL1cd3YLj8YjX11cYY8Jo1HBrg3sAYABMRKy15kdAa81JkjAAVkq5dnWf+q8ICCG8EJdlic1m8+1ObcrCenBrpq5rdF0HIkJRFIjjGACOQojVJAGLOI69vE45vBdSSrRti96nmCXwHfriGs22VmwdWcJZlgEAepe3Edhut0MkrDHr6F5YH3cRsMqPwBSBP/casR8gYwyklFBKebIsy7xirKrK0xkhPIbhsDgcDpNy98hqrTnP80mZ68NZ305Aa837/X4kV0qNzn/47kMIzOmVZfl8AkQ0/JZSeuF+CoGmaa6m4ikEmJnTNJ1MxdMItG3LUspRKp5GgJlHJ0Mp9VwCzOw5vefdhxHQWnup+BGBKQO3EmBmLorixwQaZh5uLnNG3Hynacr7/d6Th6m4lUDCfKlo9+MSKldVxcDlA0REHEXRiIDWmpVSHMcx73a72wi4UTidTt+G8TeYI0B8uSiOcvkIhH9kLgHhkIhwaVCQJAk+Pj4AAGVZYr1ej+6EYaMy1aRM9RjhndATMnPKfL0efjOklExEXBSFDUDjRcAhUQPYGGOwWq2GXVy7gNp5uVwOO7SNjLuewEoIcRw9ZWZp6+HBaHu7VZ9uTEagJ0EAcgBrfLXbtgkwwfwvWFvdYS2EuNpA/AfKwtcV55mYpwAAAABJRU5ErkJggg==';

const githubLogoBase64 =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAC5UlEQVR4AaxXA4xdQRSd2u7MfNe24qS2bRtR7b5ujFpR46SI2Ua1bdu2ufckn7Mz7739fya57e7gnPuul/ldTZo0KRkUontAiC0BzveT3JRCfIbgZ+zhDHdwl9laNTkPEPA2yflHkv8+5SPe4G3WxHXr1i1FIHkkXwCajeAtMICVzVefAIgNAZZva0SEaE7+fIyHNgWYwPb+cpXcshLgcPP5CeXBNZKdtP8mC8L39HY3yVnVHdqYQLCoIEEpp+GsQ4cOxaWUk6QQz7EfT79LBH6Q5AD9fJH2P8QJ3tLenEQqUloOUnHBVdD0mmgPBALtMmoB5+Vpr7HjOEWZsv7//18kHA7Xj8ViVdL3SfFauuzIcAVyVmfGYDDYhuW4QqFQTIcNzmSFMxUZMt/YXBUgi/U0FStwM5ROUxEhC9TIVQFySzXCeq3jADfMv8WgwEJmaVFQTjRwbMHhft1hJBKpZ62f1KxZWccBbihwU2d+RDWzuAjzoUaBmww5rVHgHrO8iOyUygNuaPZVY55PthXAR6k84NaYJi5SCosxUJp4fmgUeAAFThpSZIwlfrdUP8GCnG8wROgpW4FIRHt0HOBWm4Wq4XwLwTfBhA9u1qBBgwoIOsOlf+hcbdq0KVFYYjQsfABh/DFgf0JzS5hoc1pqrA1JOQotNs0dj+iOExKiKxWoqibS2rVrV5JSdgpKuYze33YdTqTclHwY4bwuEfyMk70DEbQjkPOa3D2CrzOYe7/PGfFHVIg6aqCsTu9U1EQitOqpjYSs08+l83XwqYCjHckUs29PG1bm0d56TEhuIzbc40UODmCYhocoXXoWv/wHvZwVctG73y4KPAOHe/8WokWGEkLsRTRTykyn/1cOHTq0mIcCf0zkwPY7RESkEBd0QJhiPBT4qzH7JYxm2fxp5qg1HHXdQ4F/acH2kyRP88b/QrpQzm5EZqB9YkT3KLtvcQ/Tjr2hJj6SY+T24z6yXv74iDUXAHnsU9JuBm1tAAAAAElFTkSuQmCC';

// const djangoLogoBase64 =
//   'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAAaVBMVEURMigAKx8AKBsKLyRmc256hYFAU0wAJhp0f3s6T0gAGADf4uH///+FkIwAAADm6OeBjIja3NsACwDFysjq7OsAEwAAIhSUnZrv8fD29/cAEACwtrTP0tEhPTTAxsRSY12co6AAHQxbamUrGWKdAAAAv0lEQVR4AWIYaACojS4SEIahIIDGE9Kpu8v974g7H3ZMvS8eLoQ83yhTWhurnN4R6gMgjGIgsQSmJwx+YJbHheNvYn0ZndBGUVS9kqzqtCnaI3b9MCj+YmOLcy4D0uIZuwDA1LfnAb0hnwGYY58U2hoIMuZJrBYgLb+g0AB4RTZ7VABNnRTkaK3DJUc8lnAvyCoztQDaWEUAFH/brrVbhe/8ugEYJaNibQPEHUWXYWPmjIwMm3ar2JfYteLszzkAujcN06GBllAAAAAASUVORK5CYII=';
// 한개의 컴포넌트로 재활용!!
function SocialIcon({ url, logo }) {
  return (
    <a href={url} target="_blank" rel="noopener noreferrer">
      <img
        src={logo}
        alt="Social Media Logo"
        style={{ width: '32px', height: '32px', margin: '0 8px' }}
      />
    </a>
  );
}

// 각 로고 Base64 변수도 함께 내보내면 다른 파일에서 쉽게 가져올 수 있다.
export {
  SocialIcon,
  instagramLogoBase64,
  notionLogoBase64,
  githubLogoBase64,
  // djangoLogoBase64,
};
