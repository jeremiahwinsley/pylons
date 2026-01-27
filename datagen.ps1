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
    },
    @{
        mod = "cobblemon"
        slug = "berry"
        count = 2
        block_template = "@_berry"
        item_template = "@_berry"
        variants = @(
            "aguav",
            "apicot",
            "aspear",
            "babiri",
            "belue",
            "bluk",
            "charti",
            "cheri",
            "chesto",
            "chilan",
            "chople",
            "coba",
            "colbur",
            "cornn",
            "custap",
            "durin",
            "eggant",
            "enigma",
            "figy",
            "ganlon",
            "grepa",
            "haban",
            "hondew",
            "hopo",
            "iapapa",
            "jaboca",
            "kasib",
            "kebia",
            "kee",
            "kelpsy",
            "lansat",
            "leppa",
            "liechi",
            "lum",
            "mago",
            "magost",
            "maranga",
            "micle",
            "nanab",
            "nomel",
            "occa",
            "oran",
            "pamtre",
            "passho",
            "payapa",
            "pecha",
            "persim",
            "petaya",
            "pinap",
            "pomeg",
            "qualot",
            "rabuta",
            "rawst",
            "razz",
            "rindo",
            "roseli",
            "rowap",
            "salac",
            "shuca",
            "sitrus",
            "spelon",
            "starf",
            "tamato",
            "tanga",
            "touga",
            "wacan",
            "watmel",
            "wepear",
            "wiki",
            "yache"
        )
    },
    @{
        mod = "cobblemon"
        slug = "mint"
        count = 2
        block_template = "@_mint"
        item_template = "@_mint_leaf"
        variants = @(
            "red",
            "blue",
            "cyan",
            "pink",
            "green",
            "white"
        )
    }
    @{
        mod = "cobblemon"
        count = 2
        block_template = "hearty_grains"
        item_template = "hearty_grains"
        variants = @("")
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
