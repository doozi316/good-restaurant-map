import Vue from 'vue';

// 0. 편의를 위해 아이콘은 알파벳 순서대로 추가하자.
// 1. 설치했던 fontawesome-svg-core 와 vue-fontawesome
import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

// 2. 설치했던 아이콘 파일에서 원하는 아이콘 불러오기
import {
  faAngleLeft,
  faAngleRight,
  faLocationDot,
} from "@fortawesome/free-solid-svg-icons";

// 3. 불러온 아이콘을 라이브러리에 담기
library.add(faAngleLeft);
library.add(faAngleRight);
library.add(faLocationDot);

// 4. fontawesome 아이콘을 Vue 템플릿에서 사용할 수 있도록 등록
Vue.component("FontAwesomeIcon", FontAwesomeIcon);
