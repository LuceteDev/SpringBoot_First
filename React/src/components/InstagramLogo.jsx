import React from 'react';

// Base64 문자열을 변수에 저장
const instagramLogoBase64 =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAABOFBMVEVHcExxF/13F/1zFf2AFv2MCu+iBOmyAuLEAd7SANraBMzwHpedI+XeANfmANHmAr+PF/6fD/vxAL7qArV7GP21FvnRVurnZ+P2Z9roQeDzGtXzB8v7AqvWiPn+7fn////7ftv/A7z0BKH3v/H+3fT+ruj+AJr/9/r/RcD/bMDKFff/Bo3iG+r8GrX/AIH+H5r/yeP+BHj/t9n/V5r+Am7+H37+BGT/ssX/Imz/RXv/ucz+e4z+JFT/XI3/MGL+LUf/u7f/yc7/Alr+OUf+MSz+QzT+R0T+iHD+SRj+VzH+WQX+AkL+c2L/0sT+aCX+cBL/7+j+aQL+eRv/oUX/fwL+iBD/wXz+jgD/3rr/1rn+dgz+nAP9FpT+lw7+qgH+tQD+vQD/w1f9kwf/xAD9PFn9ogj/ywD9uAfgLZLBAAAAaHRSTlMAW9H+////////xEsC///N////zFz/////////////////uP////7///////////7////////////////////////////////////////////////////////F/7VQ///+/87/vsj/uqL0GQwAAAHcSURBVHgBRMlFehwxEEDhV1VS04yZ7U04m+AmcBjfLLscJ4xLHyA0YGqSlJ7P9MT6BRC52EEQuDjlFITx2eWvXJsDQWYIKgJj4VQW5gBJAg75K5vTgZDTFUEu/kUEzqGaiC6DMBbhpqQyoyKTNYD1Hp+Ui6J0ZH1HhXNA0RfzyRIZl5WprroK3BZwwvx0aUGdB87HCIN5VBUVP9i468ZLxWLv+pg6vJmDaSiSW2eyIRNg84/HxfVJmTD3DxFWGmWjrosRpxMNGr3iIqYbG7IGK5lKvZE3jVuTynsDzHRoD3xpxaTQvIKumGBWgCouM9bPKEisJg0VTV4X0aBqQEuXQ2kGlmVmZCtgCoxBJfeAz0b8NQbWI7wOAKCZMgbn9E6tItG1HFTKRQ4jgSYpiiPu6BFJW1fW9J7oDFWWgpf0MH09YnB6yIEYnIJhLhqkpw3aC4OuUJ4QlDzX55/Um5m4Ue6dmXr59LwkBM0xy/ieVNV7zSSTZMU3fs8UNBomr+v3jQx5X1XmB7tdgnNu1JqDl+0nbnp+HpLp+WM5bB0mIMJFc/ouSgjd+U99O1qYONOhFEMonPcZCZ6eGp9eRcSwhWkSRCIxKnfeIMBh1WRcFOhTaENo/096OTA7AACDzKODDThakgAAAABJRU5ErkJggg==';

const InstagramLogo = () => {
  return (
    <img
      src={instagramLogoBase64}
      alt="Instagram Logo"
      style={{ width: '28px', height: '28px' }}
    />
  );
};

export default InstagramLogo;
