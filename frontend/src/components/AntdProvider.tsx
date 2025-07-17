// https://github.com/ant-design/v5-patch-for-react-19/issues/27#issuecomment-2811392450

"use client";

import "@ant-design/v5-patch-for-react-19";

import { StyleProvider } from "@ant-design/cssinjs";
import { ConfigProvider, App } from "antd";
import locale from 'antd/locale/ru_RU';
import dayjs from 'dayjs';
import 'dayjs/locale/ru';
import type { FC, PropsWithChildren } from "react";

// Настраиваем dayjs на русский язык глобально
dayjs.locale('ru');

const AntdProvider: FC<PropsWithChildren> = ({ children }) => (
  <StyleProvider layer>
    <ConfigProvider
      theme={{
        cssVar: true, hashed: false,
        token: {
          colorBorderSecondary: '#E5E5E5',
        }
      }}
      locale={locale}
    >
      <App>{children}</App>
    </ConfigProvider>
  </StyleProvider>
);

export default AntdProvider;
