# Metrics

## Similarity between pair of sources

$$
avg\_sim_s = \frac{2 \cdot \sum_{match \in tiles} length}{|A|+|B|}
$$

$$
max\_sim_s = \frac{\sum_{match \in tiles} length}{min(|A|,|B|)}
$$

## Similarity between pair of projects

$$
sim_p = max\biggl\{ \frac{|\text{reported sources of A}|}{|\text{sources of A}|}, \frac{|\text{reported sources of B|}}{|sources of B|} \biggl\} \cdot P_{75}(\text{similarity of reported sources})
$$
