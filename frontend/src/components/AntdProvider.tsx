// https://github.com/ant-design/v5-patch-for-react-19/issues/27#issuecomment-2811392450

"use client";

import "@ant-design/v5-patch-for-react-19";

import { StyleProvider } from "@ant-design/cssinjs";
import { ConfigProvider, App } from "antd";
import type { FC, PropsWithChildren } from "react";

const AntdProvider: FC<PropsWithChildren> = ({ children }) => (
  <StyleProvider layer>
    <ConfigProvider theme={{ cssVar: true, hashed: false }}>
      <App>{children}</App>
    </ConfigProvider>
  </StyleProvider>
);

export default AntdProvider;
