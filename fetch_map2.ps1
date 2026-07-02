$query = "[out:json];way['highway'~'primary|secondary|tertiary|residential'](40.75,-73.99,40.76,-73.98);out body;>;out skel qt;"
$encodedQuery = [System.Web.HttpUtility]::UrlEncode($query)
$uri = "https://overpass-api.de/api/interpreter?data=$encodedQuery"
$response = Invoke-RestMethod -Uri $uri -Method Get

$nodes = @{}
$ways = @()

foreach ($element in $response.elements) {
    if ($element.type -eq 'node') {
        $nodes[$element.id] = @{ lat = $element.lat; lon = $element.lon }
    } elseif ($element.type -eq 'way') {
        if ($element.nodes) {
            $ways += ,$element.nodes
        }
    }
}

function Get-Distance ($lat1, $lon1, $lat2, $lon2) {
    $R = 3958.8
    $dlat = [math]::PI * ($lat2 - $lat1) / 180.0
    $dlon = [math]::PI * ($lon2 - $lon1) / 180.0
    $a = [math]::Sin($dlat/2) * [math]::Sin($dlat/2) + [math]::Cos([math]::PI * $lat1 / 180.0) * [math]::Cos([math]::PI * $lat2 / 180.0) * [math]::Sin($dlon/2) * [math]::Sin($dlon/2)
    $c = 2 * [math]::Atan2([math]::Sqrt($a), [math]::Sqrt(1-$a))
    return $R * $c
}

$valid_nodes = @{}
$edges = @()

foreach ($way_nodes in $ways) {
    for ($i = 0; $i -lt ($way_nodes.Length - 1); $i++) {
        $u = $way_nodes[$i]
        $v = $way_nodes[$i+1]
        
        if ($nodes.ContainsKey($u) -and $nodes.ContainsKey($v)) {
            $valid_nodes[$u] = $true
            $valid_nodes[$v] = $true
            
            $dist = Get-Distance $nodes[$u].lat $nodes[$u].lon $nodes[$v].lat $nodes[$v].lon
            $edges += "$u,$v,$dist"
            # Overpass edges are undirected technically (usually), add reverse edge
            $edges += "$v,$u,$dist"
        }
    }
}

$nodes_csv = @()
foreach ($key in $valid_nodes.Keys) {
    $nodes_csv += "$key,$($nodes[$key].lat),$($nodes[$key].lon)"
}

$nodes_csv | Out-File -FilePath "city_nodes.csv" -Encoding UTF8
$edges | Out-File -FilePath "city_edges.csv" -Encoding UTF8

Write-Host "Saved $($valid_nodes.Count) nodes and $($edges.Count) edges."
