'use client'
import { Geist, Geist_Mono } from "next/font/google";
import AntdProvider from "@/components/AntdProvider";
// https://ant.design/docs/react/use-with-next
import { AntdRegistry } from '@ant-design/nextjs-registry';
import "./globals.css";
import { store } from '@/store/store'
import { Provider } from 'react-redux'

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${geistSans.variable} ${geistMono.variable}`}>
        <AntdRegistry>
          <Provider store={store}>
            <AntdProvider>{children}</AntdProvider>
          </Provider>
        </AntdRegistry>
      </body>
    </html>
  );
}
