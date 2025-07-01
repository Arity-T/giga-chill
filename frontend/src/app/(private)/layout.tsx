import { AuthGuard } from '@/components/auth-guard/AuthGuard';

export default function PrivateLayout({ children }: { children: React.ReactNode }) {
  return <AuthGuard>{children}</AuthGuard>;
}