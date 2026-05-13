"use client";

import { useEffect, useState } from "react";

import { adminDashboardApi } from "@/features/admin-dashboard";

type Summary = { books: number; categories: number; orders: number; promotions: number; users: number };

export default function AdminDashboardPage() {
   const [summary, setSummary] = useState<Summary | null>(null);

  useEffect(() => { adminDashboardApi.summary().then(setSummary); }, []);

  return (
    <section className="rounded-lg border p-6">
      <h2 className="text-lg font-medium">Dashboard</h2>
      {!summary ? <p className="text-sm">Loading...</p> : (
        <ul className="mt-3 space-y-2 text-sm">
          <li>Tổng số sách: {summary.books}</li>
          <li>Tổng số category: {summary.categories}</li>
          <li>Tổng số đơn hàng: {summary.orders}</li>
          <li>Tổng số promotion: {summary.promotions}</li>
          <li>Users (endpoint hiện có): {summary.users}</li>
        </ul>
      )}
    </section>
  );
}
