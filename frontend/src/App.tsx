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
      <main className="container">
        <header>
          <h1>Telco Data Integration</h1>
          <p>
            Unified product catalogue with validation
            and source conflict detection.
          </p>
        </header>

        <section className="toolbar">
          <label>
            Search
            <input
                type="search"
                value={filter}
                onChange={(e) => setFilter(e.target.value)}
                placeholder="SKU, name or manufacturer"
            />
          </label>

          <label>
            Sort by
            <select
                value={sort}
                onChange={(e) =>
                    setSort(e.target.value as ProductSort)
                }
            >
              <option value="sku">SKU</option>
              <option value="name">Name</option>
              <option value="price">Price</option>
              <option value="stock">Stock</option>
            </select>
          </label>

          <label className="checkbox-label">
            <input
                type="checkbox"
                checked={onlyValid}
                onChange={(e) => setOnlyValid(e.target.checked)}
            />
            Only valid products
          </label>
        </section>

        {loading && <p>Loading products...</p>}

        {error && (
            <p role="alert" className="error">
              {error}
            </p>
        )}

        {!loading && !error && (
            <>
              <p className="result-count">
                {products.length} products found
              </p>
              <ProductTable products={products} />
            </>
        )}
      </main>
  );
}