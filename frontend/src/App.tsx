import { useEffect, useState } from 'react';
import { fetchProducts } from './api/products';
import ProductTable from './components/ProductTable';
import type { Product, ProductSort } from './types/product';
import './App.css';

export default function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [filter, setFilter] = useState('');
  const [sort, setSort] = useState<ProductSort>('sku');
  const [onlyValid, setOnlyValid] = useState(false);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const controller = new AbortController();

    async function loadProducts() {
      setLoading(true);
      setError(null);

      try {
        const data = await fetchProducts(
            { filter, sort, onlyValid },
            controller.signal
        );

        if (!controller.signal.aborted) {
          setProducts(data);
        }
      } catch (err) {
        if (!controller.signal.aborted) {
          setError(
              err instanceof Error
                  ? err.message
                  : 'Unknown error'
          );
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    void loadProducts();

    return () => controller.abort();
  }, [filter, sort, onlyValid]);

  return (
      <div className="min-vh-100 bg-body-tertiary">
        <header className="bg-white border-bottom py-4 mb-4">
          <div className="container">
            <h1 className="h3 fw-bold mb-2">
              Telco Data Integration
            </h1>

            <p className="text-secondary mb-0">
              Unified product catalogue with validation and conflict detection.
            </p>
          </div>
        </header>

        <main className="container pb-5">
          <div className="card border-0 shadow-sm mb-4">
            <div className="card-body">
              <div className="row g-3 align-items-end">
                <div className="col-12 col-md-5">
                  <label htmlFor="product-search" className="form-label">
                    Search products
                  </label>

                  <input
                      id="product-search"
                      type="search"
                      className="form-control"
                      value={filter}
                      onChange={e => setFilter(e.target.value)}
                      placeholder="SKU, name or manufacturer"
                  />
                </div>

                <div className="col-12 col-md-3">
                  <label htmlFor="product-sort" className="form-label">
                    Sort by
                  </label>

                  <select
                      id="product-sort"
                      className="form-select"
                      value={sort}
                      onChange={e =>
                          setSort(e.target.value as ProductSort)
                      }
                  >
                    <option value="sku">SKU</option>
                    <option value="name">Name</option>
                    <option value="price">Price</option>
                    <option value="stock">Stock</option>
                  </select>
                </div>

                <div className="col-12 col-md-4">
                  <div className="form-check mb-2">
                    <input
                        id="only-valid"
                        type="checkbox"
                        className="form-check-input"
                        checked={onlyValid}
                        onChange={e => setOnlyValid(e.target.checked)}
                    />

                    <label
                        htmlFor="only-valid"
                        className="form-check-label"
                    >
                      Only valid products
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {loading && (
              <div className="d-flex align-items-center gap-2 py-4">
                <div
                    className="spinner-border spinner-border-sm text-primary"
                    role="status"
                />
                <span>Loading products...</span>
              </div>
          )}

          {error && (
              <div className="alert alert-danger" role="alert">
                {error}
              </div>
          )}

          {!loading && !error && (
              <>
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <h2 className="h5 mb-0">Products</h2>

                  <span className="badge text-bg-secondary">
              {products.length} products
            </span>
                </div>

                <ProductTable products={products} />
              </>
          )}
        </main>
      </div>
  );
}