"use client";

import { useEffect, useState } from "react";

import { adminOrdersApi, type AdminOrder } from "@/features/admin-orders";

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<AdminOrder[]>([]);
  const load = async () => setOrders(await adminOrdersApi.list());

  useEffect(() => { void load(); }, []);

  return (
    <section className="space-y-4 rounded-lg border p-6">
      <h2 className="text-lg font-medium">Orders</h2>
      {orders.map((order) => (
        <article key={order.orderId} className="flex items-center justify-between rounded border p-3 text-sm">
          <div>#{order.orderId} · {order.status}</div>
          <button type="button" className="underline" onClick={async () => { await adminOrdersApi.updateStatus(order.orderId, "SHIPPED"); await load(); }}>Chuyển SHIPPED</button>
        </article>
      ))}
    </section>
  );
}
