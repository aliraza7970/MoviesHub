package com.stc.movieshub.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.stc.movieshub.R
import com.stc.movieshub.model.Film
import com.stc.movieshub.screens.destinations.MovieDetailsDestination
import com.stc.movieshub.sharedComposables.LoopReverseLottieLoader
import com.stc.movieshub.ui.theme.AppOnPrimaryColor
import com.stc.movieshub.ui.theme.AppPrimaryColor
import com.stc.movieshub.ui.theme.ButtonColor
import com.stc.movieshub.util.Constants.BASE_BACKDROP_IMAGE_URL
import com.stc.movieshub.util.Constants.BASE_POSTER_IMAGE_URL
import com.stc.movieshub.util.FilmType
import com.stc.movieshub.viewmodel.HomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import com.stc.movieshub.sharedComposables.SearchFilm
import retrofit2.HttpException
import java.io.IOException

@Destination
@Composable
fun Home(
    navigator: DestinationsNavigator?,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF180E36))
    ) {
        ProfileAndSearchBar(navigator!!, homeViewModel)
        NestedScroll(navigator = navigator, homeViewModel)
    }
}

@Composable
fun ProfileAndSearchBar(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()

    ) {
        SearchFilm(
            autoFocus = false,
            onSearch = {
                homeViewModel.getSearchFilms(filmType = FilmType.MOVIE)
            })
    }
    Row(
        modifier = Modifier
            .padding(top = 2.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,

    ) {


        Column(
            horizontalAlignment = Alignment.Start
        ) {

            val filmTypes = listOf(FilmType.MOVIE)
            val selectedFilmType = homeViewModel.selectedFilmType.value


            Row(

            ) {
                filmTypes.forEachIndexed { index, filmType ->
                    Text(
                        text = if (filmType == FilmType.MOVIE) "Watch New Movies" else "Tv Shows",
                        fontWeight = if (selectedFilmType == filmTypes[index]) FontWeight.Bold else Light,
                        fontSize = if (selectedFilmType == filmTypes[index]) 24.sp else 16.sp,
                        color = if (selectedFilmType == filmTypes[index])
                            Color(0xFFFFA726) else Color.LightGray.copy(alpha = 0.78F),
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (homeViewModel.selectedFilmType.value != filmTypes[index]) {
                                    homeViewModel.selectedFilmType.value = filmTypes[index]
                                    homeViewModel.getFilmGenre()
                                    homeViewModel.refreshAll(null)
                                }
                            }
                    )
                }
            }

            val animOffset = animateDpAsState(
                targetValue = when (filmTypes.indexOf(selectedFilmType)) {
                    0 -> (-35).dp
                    else -> 30.dp
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            )


        }

    }
}

@Composable
fun NestedScroll(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel,
) {
    val trendingFilms = homeViewModel.trendingMoviesState.value.collectAsLazyPagingItems()
    val selectedFilmType = homeViewModel.selectedFilmType.value



    val listState: LazyListState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .fillMaxSize()


    ) {

        item {
            val genres = homeViewModel.filmGenres

            LazyRow(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                items(count = genres.size) {
                    SelectableGenreChip(
                        genre = genres[it].name,
                        selected = genres[it].name == homeViewModel.selectedGenre.value.name,
                        onclick = {
                            if (homeViewModel.selectedGenre.value.name != genres[it].name) {
                                homeViewModel.selectedGenre.value = genres[it]
                                homeViewModel.filterBySetSelectedGenre(genre = genres[it])
                            }
                        }
                    )
                }
            }
        }


        item {
            ScrollableMovieItems(
                landscape = true,
                navigator = navigator,
                pagingItems = trendingFilms,
                selectedFilmType = selectedFilmType,
                onErrorClick = {
                    homeViewModel.refreshAll()
                }
            )
        }

    }
}

@Composable
fun MovieItem(
    imageUrl: String,
    title: String,
    releaseDate: String,
    modifier: Modifier,
    landscape: Boolean,
    onclick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(
            topEnd = 2.dp,
            topStart = 0.dp,
            bottomEnd = 2.dp,
            bottomStart = 2.dp),
        modifier = Modifier
            .wrapContentHeight()
            .padding(10.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(AppPrimaryColor.copy(alpha = 0.8F))

    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .clip(RoundedCornerShape(2.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onclick()
                },
            horizontalAlignment = Alignment.Start

        ) {

            CoilImage(
                imageModel = imageUrl,
                shimmerParams = ShimmerParams(
                    baseColor = AppPrimaryColor,
                    highlightColor = ButtonColor,
                    durationMillis = 500,
                    dropOff = 0.65F,
                    tilt = 20F
                ),
                failure = {
                    Box(
                        contentAlignment = Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.image_not_available),
                            contentDescription = "no image"
                        )
                    }
                },
                previewPlaceholder = R.drawable.popcorn,
                contentScale = Crop,
                circularReveal = CircularReveal(duration = 1000),
                modifier = modifier.clip(RoundedCornerShape(8.dp)),
                contentDescription = "Movie item"
            )

            AnimatedVisibility(visible = landscape) {
                Text(
                    text = trimTitle(title),
                    modifier = Modifier
                        .padding(start = 4.dp, top = 4.dp)
                        .fillMaxWidth(),
                    maxLines = 1,
                    color = AppOnPrimaryColor,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = Normal,
                    textAlign = TextAlign.Start
                )
            }
            AnimatedVisibility(visible = landscape) {
                Text(
                    text = trimTitle(releaseDate),
                    modifier = Modifier
                        .padding(start = 4.dp, top = 4.dp)
                        .fillMaxWidth(),
                    maxLines = 1,
                    color = AppOnPrimaryColor,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = Normal,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}


private fun trimTitle(text: String) = if (text.length <= 26) text else {
    val textWithEllipsis = text.removeRange(startIndex = 26, endIndex = text.length)
    "$textWithEllipsis..."
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScrollableMovieItems(
    landscape: Boolean = false,
    selectedFilmType: FilmType,
    navigator: DestinationsNavigator,
    pagingItems: LazyPagingItems<Film>,
    onErrorClick: () -> Unit
) {
    Box(
        contentAlignment = Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (!landscape) 215.dp else 600.dp)
    ) {
        when (pagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                LoopReverseLottieLoader(lottieFile = R.raw.loader)
            }
            is LoadState.NotLoading -> {
                LazyVerticalGrid(modifier = Modifier, cells = GridCells.Fixed(2)) {
                    items(pagingItems.itemCount) { film ->
                        pagingItems[film]?.let {
                            val imagePath =
                                if (landscape) "$BASE_BACKDROP_IMAGE_URL/${it!!.backdropPath}"
                                else "$BASE_POSTER_IMAGE_URL/${it!!.posterPath}"
                            MovieItem(
                                landscape = landscape,
                                imageUrl = imagePath,
                                title = it.title,
                                releaseDate = it.releaseDate,
                                modifier = Modifier
                                    .width(if (landscape) 215.dp else 130.dp)
                                    .height(if (landscape) 161.25.dp else 195.dp)
                            ) {
                                navigator.navigate(
                                    direction = MovieDetailsDestination(it, selectedFilmType = selectedFilmType)
                                ) {
                                    launchSingleTop = true
                                }
                            }
                        }



                    }
                }
            }
            is LoadState.Error -> {
                val error = pagingItems.loadState.refresh as LoadState.Error
                val errorMessage = when (error.error) {
                    is HttpException -> "Sorry, Something went wrong!\nTap to retry"
                    is IOException -> "Connection failed. Tap to retry!"
                    else -> "Failed! Tap to retry!"
                }
                Box(contentAlignment = Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(161.25.dp) // maintain the vertical space between two categories
                        .clickable {
                            onErrorClick()
                        }
                ) {
                    Text(
                        text = errorMessage,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = Light,
                        color = Color(0xFFE28B8B),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            else -> {
            }
        }
    }
}

@Composable
fun SelectableGenreChip(
    genre: String,
    selected: Boolean,
    onclick: () -> Unit
) {

    val animateChipBackgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFFFA726) else ButtonColor.copy(alpha = 0.2F),
        animationSpec = tween(
            durationMillis = if (selected) 100 else 50,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .clip(CircleShape)
            .border(1.dp, Color(0xFFFFA726), CircleShape)
            .background(
                color = animateChipBackgroundColor
            )
            .height(32.dp)
            .widthIn(min = 80.dp)
            /*.border(
                width = 0.5.dp,
                color = Color(0xC69495B1),
                shape = CircleShape
            )*/
            .padding(horizontal = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onclick()
            }
    ) {
        Text(
            text = genre,
            fontWeight = if (selected) Normal else Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Center),
            color = if (selected) Color(0XFF180E36) else Color.White.copy(alpha = 0.80F)
        )
    }
}

@Composable
fun ShowAboutCategory(name: String, description: String) {
    var showAboutThisCategory by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = name,
            fontSize = 24.sp,
            color = AppOnPrimaryColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 4.dp, top = 14.dp,
                end = 8.dp, bottom = 4.dp
            )
        )
        IconButton(
            modifier = Modifier.padding(top = 14.dp, bottom = 4.dp),
            onClick = { showAboutThisCategory = showAboutThisCategory.not() }) {
            Icon(
                imageVector = if (showAboutThisCategory) Icons.Filled.KeyboardArrowUp else Icons.Filled.Info,
                tint = AppOnPrimaryColor,
                contentDescription = "Info Icon"
            )
        }
    }

    AnimatedVisibility(visible = showAboutThisCategory) {
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .border(
                    width = 1.dp, color = ButtonColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(ButtonColor.copy(alpha = 0.25F))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = description,
                    color = AppOnPrimaryColor
                )
            }
        }
    }
}
