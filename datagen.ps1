$ErrorActionPreference = "Stop"

$root = "$PSScriptRoot/src/main/resources/data/pylons/recipe/harvesting"

$template = @'
{
  "neoforge:conditions": [
    {
      "type": "neoforge:mod_loaded",
      "modid": ""
    }
  ],
  "type": "pylons:harvesting",
  "block": "",
  "output": {
    "count": 1,
    "id": ""
  }
}
'@

$datagen = @(
    @{
        mod = "cobblemon"
        slug = "apricorn"
        count = 1
        block_template = "@_apricorn"
        item_template = "@_apricorn"
        variants = @(
            "black",
            "blue",
            "green",
            "pink",
            "red",
            "white",
            "yellow"
        )
    }
)

foreach ($entry in $datagen) {
    foreach ($variant in $entry.variants) {
        $mod = $entry.mod
        $slug = $entry.slug
        $block = $entry.block_template.Replace("@", $variant)
        $item = $entry.item_template.Replace("@", $variant)

        $json = ConvertFrom-Json -InputObject $template;
        $json.'neoforge:conditions'[0].modid = $entry.mod
        $json.block = "${mod}:$block";
        $json.output.count = $entry.count
        $json.output.id = "${mod}:$item"

        $folder = "$root/$mod/$slug"
        New-Item -Path $folder -Type Directory -Force | Out-Null
        ConvertTo-Json -Depth 32 -InputObject $json | Set-Content "$folder/$block.json"
    }
}
