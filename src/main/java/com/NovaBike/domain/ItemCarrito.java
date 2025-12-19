
package com.NovaBike.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemCarrito implements Serializable {

    private Producto producto;
    private int cantidad;
}