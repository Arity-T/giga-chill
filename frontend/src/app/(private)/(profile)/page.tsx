'use client';

import styles from "./page.module.css";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import Link from "next/link";
import { PAGES } from "@/config/pages.config";

export default function ProfileContent() {
  const router = useRouter();

  useEffect(() => {
    router.replace(PAGES.EVENTS);
  }, [router]);

  return (
    <div className={styles.page}>
      Редирект на <Link href={PAGES.EVENTS}>events</Link>...
    </div>
  );
}
