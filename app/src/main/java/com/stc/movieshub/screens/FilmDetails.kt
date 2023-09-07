package com.stc.movieshub.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.stc.movieshub.R
import com.stc.movieshub.model.Film
import com.stc.movieshub.model.Genre

import com.stc.movieshub.sharedComposables.BackButton
import com.stc.movieshub.sharedComposables.ExpandableText
import com.stc.movieshub.sharedComposables.MovieGenreChip
import com.stc.movieshub.ui.theme.AppOnPrimaryColor
import com.stc.movieshub.ui.theme.AppPrimaryColor
import com.stc.movieshub.ui.theme.ButtonColor
import com.stc.movieshub.util.Constants.BASE_BACKDROP_IMAGE_URL
import com.stc.movieshub.util.Constants.BASE_POSTER_IMAGE_URL
import com.stc.movieshub.util.FilmType
import com.stc.movieshub.viewmodel.DetailsViewModel
import com.stc.movieshub.viewmodel.HomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import java.text.SimpleDateFormat
import java.util.*

@Destination
@Composable
fun MovieDetails(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel = hiltViewModel(),
    detailsViewModel: DetailsViewModel = hiltViewModel(),
    currentFilm: Film,
    selectedFilmType: FilmType
) {
    var film by remember {
        mutableStateOf(currentFilm)
    }
    val filmType: FilmType = remember { selectedFilmType }

    val date = SimpleDateFormat.getDateTimeInstance().format(Date())

    val detailsFilms = detailsViewModel.detailsMovies.value

    LaunchedEffect(key1 = film) {
        detailsViewModel.getDetailsFilms(filmId = film.id, filmType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF180E36))
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.33F)
        ) {
            val (
                backdropImage,
                backButton,
                movieTitleBox,
                moviePosterImage,
                translucentBr
            ) = createRefs()

            CoilImage(
                imageModel = "$BASE_BACKDROP_IMAGE_URL${film.backdropPath}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .fillMaxHeight()
                    .constrainAs(backdropImage) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                failure = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.backdrop_not_available),
                            contentDescription = "no image"
                        )
                    }
                },
                shimmerParams = ShimmerParams(
                    baseColor = AppPrimaryColor,
                    highlightColor = ButtonColor,
                    durationMillis = 500,
                    dropOff = 0.65F,
                    tilt = 20F
                ),
                contentScale = Crop,
                contentDescription = "Header backdrop image",
            )

            BackButton(modifier = Modifier
                .constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                }) {
                navigator.navigateUp()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0XFF180E36).copy(alpha = 0.5F),
                                Color(0XFF180E36)
                            ),
                            startY = 0.1F
                        )
                    )
                    .constrainAs(translucentBr) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(backdropImage.bottom)
                    }
            )

            Column(
                modifier = Modifier.constrainAs(movieTitleBox) {
                    start.linkTo(moviePosterImage.end, margin = 12.dp)
                    end.linkTo(parent.end, margin = 12.dp)
                    bottom.linkTo(moviePosterImage.bottom, margin = 10.dp)
                    top.linkTo(backdropImage.bottom)
                },
                verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
            ) {


                Text(
                    text = film.title,
                    modifier = Modifier
                        .padding(top = 2.dp, start = 4.dp, bottom = 4.dp)
                        .fillMaxWidth(0.5F),
                    maxLines = 2,
                    fontSize = 18.sp,
                    fontWeight = Bold,
                    color = Color.White.copy(alpha = 0.78F)
                )

                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    val filmGenres: List<Genre> = homeViewModel.filmGenres.filter { genre ->
                        return@filter if (film.genreIds.isNullOrEmpty()) false else
                            film.genreIds!!.contains(genre.id)
                    }
                    filmGenres.forEach { genre ->
                        item {
                            MovieGenreChip(
                                background = ButtonColor,
                                textColor = AppOnPrimaryColor,
                                genre = genre.name
                            )
                        }
                    }
                }


            }

            CoilImage(
                imageModel = "$BASE_POSTER_IMAGE_URL/${film.posterPath}",
                modifier = Modifier
                    .padding(26.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .width(115.dp)
                    .height(172.5.dp)
                    .constrainAs(moviePosterImage) {
                        top.linkTo(backdropImage.bottom)
                        start.linkTo(parent.start)
                    }, failure = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.image_not_available),
                            contentDescription = "no image"
                        )
                    }
                },
                shimmerParams = ShimmerParams(
                    baseColor = AppPrimaryColor,
                    highlightColor = ButtonColor,
                    durationMillis = 500,
                    dropOff = 0.65F,
                    tilt = 20F
                ),
                previewPlaceholder = R.drawable.popcorn,
                contentScale = Crop,
                circularReveal = CircularReveal(duration = 1000),
                contentDescription = "movie poster"
            )

        }
        Box(
            modifier = Modifier
                .padding(top = (230).dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Column {
                ExpandableText(
                    text = film.overview,
                    modifier = Modifier
                        .padding(top = (4).dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                        .fillMaxWidth()


                )
                Text(
                    text = "HomePage: " + detailsFilms?.homepage,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 4.dp, bottom = 4.dp)

                )
                Box(
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 30.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center

                        ) {
                            Text(
                                text = "Status: " + detailsFilms?.status,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier

                            )
                            Text(
                                text = "Budget: " + detailsFilms?.budget+" $",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier

                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.Center

                        ) {
                            Text(
                                text = "Runtime:" + detailsFilms?.runtime + " minutes",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "Revenue: " + detailsFilms?.revenue + " $",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier,
                                textAlign = TextAlign.End
                            )
                        }

                    }
                }
            }

        }

    }
}


