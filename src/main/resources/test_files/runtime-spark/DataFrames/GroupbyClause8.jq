(:JIQS: ShouldRun; Output="(Czech, Serbian, Russian)" :)
for $i in json-file("./src/main/resources/queries/conf-ex.json", 10)
group by $target := $i.target
return $target

(: variables defined in groupby are accessible all the way to and including the return clause:)
