function advent-template () {
  year=$1
  day=$(printf "%02d" $2)
  projectRoot=/Users/dan/code/aoc
  srcRoot=$projectRoot/src/main/kotlin
  packageDir=year$year/day$day
  dirname=$srcRoot/$packageDir
  filepath=$dirname/Day$day.kt
  echo Creating $filepath
  mkdir $dirname
  printf "package $(echo $packageDir | tr / .)\n\n" > $filepath
  cat $srcRoot/Template.kt >> $filepath

  url=https://adventofcode.com/$year/day/$2/input
  targetPath=$projectRoot/inputs/year$year/day$day.txt
  echo Downloading input from $url and saving to $targetPath using cookie ${ADVENT_SESSION:0:10}...
  curl $url --cookie "session=$ADVENT_SESSION" > $targetPath
}

export ADVENT_SESSION=53616c7465645f5f3614899aca4b04a4bd7241eef35aeccd3329a1ab366e2dc1103012d0e9400ec6ebc984a98a1c7d061977473c59612cd8e5b681a6934e4fcb
