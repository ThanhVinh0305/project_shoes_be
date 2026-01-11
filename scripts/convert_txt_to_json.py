import re
import json

input_file = "DANH_SACH_200_SAN_PHAM.txt"
output_file = "products_200.json"

products = []

pattern = re.compile(r"ID: (\d+) \| Tên: (.*?) \| Mã: (.*?) \| Giới tính: (\d+) \| Màu: (.*?) \| Giá: (\d+) VNĐ \| ?(.*?)\| ?(.*)")

with open(input_file, "r", encoding="utf-8") as f:
    for line in f:
        match = pattern.match(line.strip())
        if match:
            id = int(match.group(1))
            name = match.group(2).strip()
            code = match.group(3).strip()
            gender = int(match.group(4))
            color = match.group(5).strip()
            price = int(match.group(6))
            thumbnail = match.group(7).strip()
            description = match.group(8).strip()
            product = {
                "id": id,
                "name": name,
                "code": code,
                "gender": gender,
                "color": color,
                "price": price,
                "thumbnail": thumbnail,
                "description": description
            }
            if 1 <= id <= 125:
                products.append(product)

# Sắp xếp theo id để đảm bảo đủ thứ tự
products = sorted(products, key=lambda x: x["id"])

with open(output_file, "w", encoding="utf-8") as f:
    json.dump(products, f, ensure_ascii=False, indent=2)

print(f"Đã xuất {len(products)} sản phẩm từ id 1 đến 125 sang {output_file}")
