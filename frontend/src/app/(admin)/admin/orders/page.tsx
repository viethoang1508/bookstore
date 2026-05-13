"use client";

import { useEffect, useState } from "react";

import { adminOrdersApi, type AdminOrder } from "@/features/admin-orders";
import { ApiError } from "@/lib/api-client/errors";

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<AdminOrder[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [updatingOrderId, setUpdatingOrderId] = useState<number | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);

    try {
      setOrders(await adminOrdersApi.list());
    } catch (e) {
      if (e instanceof ApiError && e.status === 404) {
        setError("API quản lý đơn hàng chưa khả dụng trong môi trường hiện tại (404 Not Found).");
      } else {
        setError("Không thể tải danh sách đơn hàng. Vui lòng thử lại.");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { void load(); }, []);

  return (
    <section className="space-y-4 rounded-lg border p-6">
      <h2 className="text-lg font-medium">Orders</h2>
      {loading ? <p className="text-sm text-muted-foreground">Đang tải đơn hàng...</p> : null}
      {error ? <p className="text-sm text-red-600">{error}</p> : null}
      {!loading && !error && orders.length === 0 ? <p className="text-sm text-muted-foreground">Chưa có đơn hàng nào.</p> : null}
      {orders.map((order) => (
        <article key={order.orderId} className="flex items-center justify-between rounded border p-3 text-sm">
          <div>#{order.orderId} · {order.status}</div>
          <button
            type="button"
            className="underline disabled:cursor-not-allowed disabled:opacity-50"
            disabled={updatingOrderId === order.orderId}
            onClick={async () => {
              setError(null);
              setUpdatingOrderId(order.orderId);

              try {
                await adminOrdersApi.updateStatus(order.orderId, "SHIPPED");
                await load();
              } catch (e) {
                if (e instanceof ApiError && e.status === 404) {
                  setError("API cập nhật trạng thái đơn hàng chưa khả dụng trong môi trường hiện tại (404 Not Found).");
                } else {
                  setError("Không thể cập nhật trạng thái đơn hàng. Vui lòng thử lại.");
                }
              } finally {
                setUpdatingOrderId(null);
              }
            }}
          >
            {updatingOrderId === order.orderId ? "Đang cập nhật..." : "Chuyển SHIPPED"}
          </button>
        </article>
      ))}
    </section>
  );
}
