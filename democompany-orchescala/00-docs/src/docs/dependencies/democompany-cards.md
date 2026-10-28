
# democompany-cards
Approved by Peter Blank (2026-10-08)


_Updated for Release ${release.tag} - created on ${created.day}_


_**[API Documentation](../../democompany-cards/OpenApi.html)**_

## democompany-cards:0.1


<div>
      <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>

      <script>
        mermaid.initialize(
        {startOnLoad: true}
        );
      </script>
<div class="mermaid">
%%{init: {"flowchart": {"htmlLabels": true, "curve": "cardinal", "useMaxWidth": true, "rankSpacing": 70}}
}%%


flowchart TB

     
    democompany-cards:0.1 -->
    democompany-services:0.1


    subgraph customer
    democompany-cards:0.1(democompany-cards:0.1)
    end
    

    subgraph general
    democompany-services:0.1(democompany-services:0.1)
    end
    

   style customer fill:#efeafb,color:#7252ac,stroke:#7252ac;
   style general fill:#ddd,color:green,stroke:green;
   linkStyle default stroke:#999,color:red;

    click democompany-cards:0.1 href "../../democompany-cards/OpenApi.html" "democompany-cards API Documentation"
    style democompany-cards:0.1 fill:#c8feda
    
    style democompany-services:0.1 fill:#ffffcc




      </div>
</div>



<div class="colorLegend">
  <p>Color Legend:</p>
  <ul>
<li style="background: #c8feda;">democompany-cards: #c8feda</li>
<li style="background: #d9d2f5;">democompany-customer: #d9d2f5</li>
<li style="background: #dbe8fb;">democompany-patterns: #dbe8fb</li>
<li style="background: #ffffcc;">democompany-services: #ffffcc</li>
  </ul>
</div>

