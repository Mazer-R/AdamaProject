package com.adama.backoffice.dataDemoInit;

import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.repository.ProductRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private String randomFrom(List<String> list) {
        return list.get((int) (Math.random() * list.size()));
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) return; // Evitar duplicados

        List<String> brands = List.of(
                "Samsung",
                "Apple",
                "Logitech",
                "Nvidia",
                "Asus",
                "HP",
                "Lenovo",
                "MSI",
                "Acer",
                "Intel",
                "AMD",
                "Seagate",
                "WesternDigital",
                "Crucial",
                "Kingston",
                "Corsair",
                "Razer",
                "CoolerMaster",
                "Adobe",
                "Microsoft",
                "OpenAI");

        List<String> types = List.of("Computer", "Peripheral", "Phone");

        for (int i = 1; i <= 50; i++) {
            Product product = new Product();
            product.setName("Producto " + i);
            product.setStatus(Product.Status.STOCK);
            product.setDescription("Descripción del producto " + i);
            product.setType(randomFrom(types));
            product.setBrand(randomFrom(brands));
            product.setStatus(Product.Status.STOCK);
            product.setModel("Modelo-" + i);


            productRepository.save(product);
        }

        System.out.println("✅ 50 productos de prueba insertados.");
    }
}
