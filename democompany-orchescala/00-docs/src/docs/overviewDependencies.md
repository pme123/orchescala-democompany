
# Dependencies Overview
Approved by Peter Blank (2026-10-08)


_Updated for Release ${release.tag} - created on ${created.day}_


## BPMN Projects

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

   democompany-services
   
   style democompany-services fill:#ffffcc


   democompany-cards --> democompany-services
   click democompany-cards href "./dependencies/democompany-cards.html" "democompany-cards Dependencies"
   style democompany-cards fill:#c8feda


   democompany-patterns
   
   style democompany-patterns fill:#dbe8fb


   democompany-customer --> democompany-services
   click democompany-customer href "./dependencies/democompany-customer.html" "democompany-customer Dependencies"
   style democompany-customer fill:#d9d2f5


    subgraph customer
    democompany-cards(democompany-cards) & democompany-customer(democompany-customer)
    end
    


    subgraph general
    democompany-services(democompany-services) & democompany-patterns(democompany-patterns)
    end
    
   style customer fill:#efeafb,color:#7252ac,stroke:#7252ac;
   style general fill:#ddd,color:green,stroke:green;
   linkStyle default stroke:#999,color:red;


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



## customer
- **democompany-cards:** [API Doc](../democompany-cards/OpenApi.html "democompany-cards API Documentation") - [Dependencies](../2026-10/dependencies/democompany-cards.html "democompany-cards Dependencies")
- **democompany-customer:** [API Doc](../democompany-customer/OpenApi.html "democompany-customer API Documentation") - [Dependencies](../2026-10/dependencies/democompany-customer.html "democompany-customer Dependencies")


## general
- **democompany-services:** [API Doc](../democompany-services/OpenApi.html "democompany-services API Documentation") 
- **democompany-patterns:** [API Doc](../democompany-patterns/OpenApi.html "democompany-patterns API Documentation") 

