import type { Product } from '../types/product';

interface Props {
    products: Product[];
}

const formatPrice = (price: number | null) =>
    price == null
        ? '—'
        : `${price.toLocaleString('hu-HU')} Ft`;

export default function ProductTable({ products }: Props) {
    if (products.length === 0) {
        return <p className="empty">No matching products found.</p>;
    }

    return (
        <div className="table-container">
            <table>
                <thead>
                <tr>
                    <th>SKU</th>
                    <th>Name</th>
                    <th>Manufacturer</th>
                    <th>Price</th>
                    <th>Stock</th>
                    <th>Status</th>
                </tr>
                </thead>

                <tbody>
                {products.map((product, index) => {
                    const primary = product.records[0];

                    return (
                        <tr key={`${product.sku ?? 'missing'}-${index}`}>
                            <td>{product.sku ?? '—'}</td>
                            <td>{primary.name ?? '—'}</td>
                            <td>{primary.manufacturer ?? '—'}</td>
                            <td>{formatPrice(primary.finalPriceHuf)}</td>
                            <td>{primary.stock ?? '—'}</td>
                            <td>
                                <div className="status">
                    <span
                        className={
                            product.valid
                                ? 'badge valid'
                                : 'badge invalid'
                        }
                    >
                      {product.valid ? 'Valid' : 'Invalid'}
                    </span>

                                    {product.hasConflicts && (
                                        <span className="badge conflict">
                        Conflict
                      </span>
                                    )}

                                    <details>
                                        <summary>Details</summary>

                                        <div className="details-content">
                                            <strong>Source records</strong>

                                            {product.records.map((record, recordIndex) => (
                                                <div
                                                    key={recordIndex}
                                                    className="source-record"
                                                >
                                                    <strong>{record.source}</strong>
                                                    <p>Name: {record.name ?? '—'}</p>
                                                    <p>Manufacturer: {record.manufacturer ?? '—'}</p>
                                                    <p>Price: {formatPrice(record.finalPriceHuf)}</p>
                                                    <p>Stock: {record.stock ?? '—'}</p>
                                                    <p>EAN: {record.ean ?? '—'}</p>
                                                    <p>Updated: {record.updatedAt ?? '—'}</p>

                                                    {record.errors.length > 0 && (
                                                        <div className="issues">
                                                            Errors: {record.errors.join(', ')}
                                                        </div>
                                                    )}
                                                </div>
                                            ))}

                                            {product.conflicts.length > 0 && (
                                                <>
                                                    <strong>Conflicts</strong>

                                                    {product.conflicts.map((conflict, i) => (
                                                        <div key={i} className="issues">
                                                            {conflict.type}: {conflict.firstSource}
                                                            {' = '}
                                                            {conflict.firstValue ?? '—'}
                                                            {' / '}
                                                            {conflict.secondSource}
                                                            {' = '}
                                                            {conflict.secondValue ?? '—'}
                                                        </div>
                                                    ))}
                                                </>
                                            )}
                                        </div>
                                    </details>
                                </div>
                            </td>
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </div>
    );
}