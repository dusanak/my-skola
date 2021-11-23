use image::io::Reader as ImageReader;
use image::GenericImageView;

fn pixel_value(pixel: image::Rgba<u8>) -> u32 {
    let mut result: u32 = 0;

    for i in 0..4 {
        result += pixel[i] as u32;
    }

    result
}

fn get_seam(x: u32, image: &image::DynamicImage) -> Vec<(u32, u32)> {
    let result = vec![(x, 0); image.dimensions().1 as usize];
    let mut last_coords = (x, 0);

    for y in 1..image.dimensions().1 - 1 {
        let mut best_pixel = image.get_pixel(last_coords.0, y);

        if image.in_bounds(last_coords.0 - 1, y) {
            if pixel_value(image.get_pixel(last_coords.0 - 1, y)) < pixel_value(best_pixel) {
                best_pixel = image.get_pixel(last_coords.0 - 1, y);
            }
        }

        if image.in_bounds(last_coords.0 + 1, y) {
            if pixel_value(image.get_pixel(last_coords.0 + 1, y)) < pixel_value(best_pixel) {
                best_pixel = image.get_pixel(last_coords.0 + 1, y);
            }
        }

        result[y as usize] = ;
    }

    result
}

fn main() {
    //let img_colors = ImageReader::open("./data/milka.jpg").unwrap().decode().unwrap();
    let img_bw = ImageReader::open("./data/bw_milka.ppm").unwrap().decode().unwrap();

    //println!("Image colors dimensions {}x{}", img_colors.dimensions().0, img_colors.dimensions().1);
    println!("Image black and white dimensions {}x{}", img_bw.dimensions().0, img_bw.dimensions().1);

    for (x, y, val) in img_bw.pixels() {
        println!("{}", pixel_value(val));

        if (x > 10) || (y > 10) {
            break;
        } 
    }
    
    //img.save("./data/test.jpg").unwrap();
}
