import urllib.request
import json
import math

# Overpass API query for a small bounding box in Manhattan (Times Square area)
query = """
[out:json];
(
  way["highway"~"primary|secondary|tertiary|residential"](40.75,-73.99,40.76,-73.98);
);
out body;
>;
out skel qt;
"""

url = "https://overpass-api.de/api/interpreter"
req = urllib.request.Request(url, data=query.encode('utf-8'))
response = urllib.request.urlopen(req)
data = json.loads(response.read().decode('utf-8'))

nodes = {}
ways = []

for element in data['elements']:
    if element['type'] == 'node':
        nodes[element['id']] = {'lat': element['lat'], 'lon': element['lon']}
    elif element['type'] == 'way':
        if 'nodes' in element:
            ways.append(element['nodes'])

# Haversine formula to calculate distance in miles
def calculate_distance(lat1, lon1, lat2, lon2):
    R = 3958.8  # Earth radius in miles
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat/2)**2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    return R * c

# Extract edges and clean up nodes
valid_nodes = set()
edges = []

for way_nodes in ways:
    for i in range(len(way_nodes) - 1):
        u = way_nodes[i]
        v = way_nodes[i+1]
        if u in nodes and v in nodes:
            valid_nodes.add(u)
            valid_nodes.add(v)
            dist = calculate_distance(nodes[u]['lat'], nodes[u]['lon'], nodes[v]['lat'], nodes[v]['lon'])
            edges.append((u, v, dist))

# Save to CSV
with open("city_nodes.csv", "w") as f:
    for n in valid_nodes:
        f.write(f"{n},{nodes[n]['lat']},{nodes[n]['lon']}\n")

with open("city_edges.csv", "w") as f:
    for u, v, d in edges:
        f.write(f"{u},{v},{d}\n")

print(f"Saved {len(valid_nodes)} nodes and {len(edges)} edges.")
